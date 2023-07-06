package com.articles.NewsDownloader.controller;

import com.articles.NewsDownloader.exception.ArticleProcessingException;
import com.articles.NewsDownloader.model.Article;
import com.articles.NewsDownloader.repository.ArticleRepository;
import lombok.extern.log4j.Log4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;

@Service
@Log4j
public class ArticleDownloadAndSaveService {
    @Autowired
    private ExecutorService executorService;
    private final ArticleRepository articleRepository;

    public ArticleDownloadAndSaveService(ArticleRepository articleRepository) {
        this.articleRepository = articleRepository;
    }

    public void downloadAndSaveArticles(ConcurrentLinkedQueue<Article> queue) {
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        Article article;
        while ((article = queue.poll()) != null) {
            Article finalArticle = article;
            futures.add(
                    CompletableFuture.runAsync(() -> {
                        try {
                            log.info("Downloading content for article with URL: " + finalArticle.getUrl());
                            String content = downloadArticle(finalArticle.getUrl());
                            finalArticle.setArticleContent(content);
                            log.info("Saving article with title: " + finalArticle.getTitle());
                            articleRepository.save(finalArticle);
                        } catch (Exception ex) {
                            log.error("Error occurred while downloading or saving the article: " + ex.getMessage());
                            throw new ArticleProcessingException("Error occurred while downloading or saving the article.", ex);
                        }
                    }, executorService)
            );
        }
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
    }

    @Retryable(maxAttemptsExpression = "${news-downloader.content-download-repeat-attempts}",
            backoff = @Backoff(delayExpression = "${news-downloader.retry-backoff-delay}"))
    private synchronized String downloadArticle(String url) {
        try {
            Document document = Jsoup.connect(url).get();
            return document.body().text();
        } catch (IOException e) {
            throw new ArticleProcessingException("Error fetching content from URL: " + url, e);
        }
    }
}