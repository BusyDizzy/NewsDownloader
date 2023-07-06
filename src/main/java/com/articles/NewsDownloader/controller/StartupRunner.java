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
import org.springframework.stereotype.Component;

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
    @Value("${thread-pool.count}")
    private int threadCount;
    @Value("${thread-pool.buffer-limit}")
    private int bufferLimit;  // Maximum number of articles in the buffer for each site

    @Value("${thread-pool.fetching-timeout}")
    private int fetchingTimeout;  // Maximum number of articles in the buffer for each site
    @Value("${news-downloader.articles.limit}")
    private int articlesPerThreadLimit;

    @Value("${news-downloader.articles.total}")
    private int totalRecordsToDownload;
    @Value("${news-downloader.blacklist}")
    private String blacklistFilePath;
    @Value("${news-downloader.article-url}")
    private String articlesAPIUrl;

    private final AtomicInteger offset = new AtomicInteger(0);

    // Buffer for storing articles, one queue per site
    private final ConcurrentHashMap<String, ConcurrentLinkedQueue<Article>> buffer = new ConcurrentHashMap<>();

    public StartupRunner(NewsAPIFetcher newsAPIFetcher, ArticleRepository articleRepository) {
        this.newsAPIFetcher = newsAPIFetcher;
        this.articleRepository = articleRepository;
    }

    @Override
    public void run(ApplicationArguments args) {
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);

        for (int i = 0; i < threadCount; i++) {
            log.info("Thread " + i + " has started");
            executorService.submit(this::fetchAndProcessArticles);
        }

        // shut down the executor service so no more tasks can be submitted
        executorService.shutdown();

        try {
            // wait for all fetching tasks to complete
            if (!executorService.awaitTermination(fetchingTimeout, TimeUnit.MINUTES)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException ex) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }

        // process any remaining articles in the buffer
        for (ConcurrentLinkedQueue<Article> queue : buffer.values()) {
            downloadAndSaveArticles(queue);
        }
    }

    private void fetchAndProcessArticles() {
        int currentOffset;
        while ((currentOffset = offset.getAndAdd(articlesPerThreadLimit)) < totalRecordsToDownload) {
            String apiUrl = String.format("%s?_limit=%d&_start=%d", articlesAPIUrl, articlesPerThreadLimit, currentOffset);

            List<ArticleDTO> articleDTOs = newsAPIFetcher.fetchArticles(apiUrl);

            List<Article> articles = ArticlesUtil.convertToArticleList(articleDTOs);
            articles.removeIf(article -> isTitleBlacklisted(article.getTitle()));

            articles.sort(Comparator.comparing(Article::getPublishedAt));

            for (Article article : articles) {
                ConcurrentLinkedQueue<Article> queue = buffer.computeIfAbsent(article.getNewsSiteName(), k -> new ConcurrentLinkedQueue<>());

                queue.add(article);

                if (queue.size() >= bufferLimit) {
                    log.info("Starting download of the queue in buffer");
                    downloadAndSaveArticles(queue);
                }
            }
        }
        // process any remaining articles in the buffer
        log.info("Finalizing download of articles which queue doesn't exceed buffer size");
        for (ConcurrentLinkedQueue<Article> queue : buffer.values()) {
            downloadAndSaveArticles(queue);
        }
    }


    private boolean isTitleBlacklisted(String title) {
        List<String> blacklistedWords;
        try {
            blacklistedWords = BlacklistUtil.loadBlacklistedWords(blacklistFilePath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return blacklistedWords.stream().anyMatch(title::contains);
    }

    private void downloadAndSaveArticles(ConcurrentLinkedQueue<Article> queue) {
        List<Article> articles = new ArrayList<>();

        Article article;
        while ((article = queue.poll()) != null) {
            String content = downloadArticle(article.getUrl());
            log.info("Downloading articles content of the following url: " + article.getUrl());
            article.setArticleContent(content);
            articles.add(article);
        }

        articleRepository.saveAll(articles);
    }

    private synchronized String downloadArticle(String url) {
        try {
            Document document = Jsoup.connect(url).get();
            return document.body().text();
        } catch (IOException e) {
            throw new RuntimeException("Error fetching content from URL: " + url, e);
        }
    }
}