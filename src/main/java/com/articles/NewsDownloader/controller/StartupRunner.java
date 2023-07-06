package com.articles.NewsDownloader.controller;

import com.articles.NewsDownloader.dto.ArticleDTO;
import com.articles.NewsDownloader.model.Article;
import com.articles.NewsDownloader.repository.ArticleRepository;
import com.articles.NewsDownloader.service.NewsAPIFetcher;
import com.articles.NewsDownloader.util.ArticlesUtil;
import com.articles.NewsDownloader.util.BlacklistUtil;
import lombok.extern.log4j.Log4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.Resource;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@Log4j
public class StartupRunner implements ApplicationRunner {

    private final NewsAPIFetcher newsAPIFetcher;
    private final ArticleRepository articleRepository;
    private final ExecutorService executorService;
    private final List<String> blacklistedWords;
    @Value("${thread-pool.count}")
    private int threadCount;
    @Value("${thread-pool.buffer-limit}")
    private int bufferLimit;  // Maximum number of articles in the buffer for each site
    @Value("${news-downloader.articles.limit}")
    private int articlesPerThreadLimit;
    @Value("${news-downloader.articles.total}")
    private int totalRecordsToDownload;
    @Value("${news-downloader.article-url}")
    private String articlesAPIUrl;
    private final AtomicInteger offset = new AtomicInteger(0);

    // Buffer for storing articles, one queue per site
    private final ConcurrentHashMap<String, ConcurrentLinkedQueue<Article>> buffer = new ConcurrentHashMap<>();

    private final RestTemplate restTemplate = new RestTemplate();

    public StartupRunner(NewsAPIFetcher newsAPIFetcher,
                         ArticleRepository articleRepository,
                         @Value("${thread-pool.count}") int threadCount,
                         @Value("${news-downloader.blacklist}") Resource blacklistResource) {
        this.newsAPIFetcher = newsAPIFetcher;
        this.articleRepository = articleRepository;
        this.executorService = Executors.newFixedThreadPool(threadCount);
        try {
            this.blacklistedWords = BlacklistUtil.loadBlacklistedWords(blacklistResource);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void run(ApplicationArguments args) {
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        for (int i = 0; i < threadCount; i++) {
            log.info("Thread " + i + " has started");
            futures.add(CompletableFuture.runAsync(this::fetchAndProcessArticles, executorService));
        }
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        // shut down the executor service so no more tasks can be submitted
        executorService.shutdown();
        log.info("Finishing run");
    }

    @Retryable(maxAttemptsExpression = "${news-downloader.content-download-repeat-attempts}",
            backoff = @Backoff(delayExpression = "${news-downloader.retry-backoff-delay}"))
    private void fetchAndProcessArticles() {
        int currentOffset;
        while ((currentOffset = offset.getAndAdd(articlesPerThreadLimit)) < totalRecordsToDownload) {
            String apiUrl = String.format("%s?_limit=%d&_start=%d", articlesAPIUrl, articlesPerThreadLimit, currentOffset);

            List<ArticleDTO> articleDTOs = newsAPIFetcher.fetchArticles(restTemplate, apiUrl);

            List<Article> articles = ArticlesUtil.convertToArticleList(articleDTOs);
            articles.removeIf(article -> isTitleBlacklisted(article.getTitle()));

            articles.sort(Comparator.comparing(Article::getPublishedAt));

            for (Article article : articles) {
                ConcurrentLinkedQueue<Article> queue = buffer.computeIfAbsent(article.getNewsSiteName(), k -> new ConcurrentLinkedQueue<>());

                queue.add(article);

                if (queue.size() >= bufferLimit) {
                    log.info("Starting download of the queue in buffer for site: " + article.getNewsSiteName());
                    downloadAndSaveArticles(queue);
                }
            }

            log.info("Save articles if anything left in buffer");
            for (ConcurrentLinkedQueue<Article> queue : buffer.values()) {
                downloadAndSaveArticles(queue);
            }
        }
    }

    private boolean isTitleBlacklisted(String title) {
        return blacklistedWords.stream().anyMatch(title::contains);
    }

    // In case of any errors occurred during content download we repeat the attempt using @Retryable
    @Retryable(maxAttemptsExpression = "${news-downloader.content-download-repeat-attempts}",
            backoff = @Backoff(delayExpression = "${news-downloader.retry-backoff-delay}"))
    private void downloadAndSaveArticles(ConcurrentLinkedQueue<Article> queue) {
        Article article;
        while ((article = queue.poll()) != null) {
            Article finalArticle = article;
            CompletableFuture.runAsync(() -> {
                try {
                    log.info("Downloading content for article with URL: " + finalArticle.getUrl());
                    String content = downloadArticle(finalArticle.getUrl());
                    finalArticle.setArticleContent(content);
                    log.info("Saving article with title: " + finalArticle.getTitle());
                    articleRepository.save(finalArticle);
                } catch (Exception ex) {
                    log.error("Error occurred while downloading or saving the article: " + ex.getMessage());
                    throw new RuntimeException(ex);
                }
            }, executorService).exceptionally(ex -> {
                log.error("Exception occurred in CompletableFuture", ex);
                throw new RuntimeException(ex);
            });
        }
    }

    @Retryable(maxAttemptsExpression = "${news-downloader.content-download-repeat-attempts}",
            backoff = @Backoff(delayExpression = "${news-downloader.retry-backoff-delay}"))
    private synchronized String downloadArticle(String url) {
        try {
            Document document = Jsoup.connect(url).get();
            return document.body().text();
        } catch (IOException e) {
            throw new RuntimeException("Error fetching content from URL: " + url, e);
        }
    }
}