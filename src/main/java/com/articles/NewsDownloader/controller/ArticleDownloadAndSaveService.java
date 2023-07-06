package com.articles.NewsDownloader.controller;

import com.articles.NewsDownloader.exception.ArticleProcessingException;
import com.articles.NewsDownloader.model.Article;
import com.articles.NewsDownloader.repository.ArticleRepository;
import lombok.extern.log4j.Log4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

@Service
@Log4j
public class ArticleDownloadAndSaveService {
    @Autowired
    private ExecutorService executorService;
    private final ArticleRepository articleRepository;

    @Autowired
    private RetryTemplate retryTemplate;

    public ArticleDownloadAndSaveService(ArticleRepository articleRepository) {
        this.articleRepository = articleRepository;
    }

    public void downloadAndSaveArticles(ConcurrentLinkedQueue<Article> queue) {
        List<Future<Void>> futures = new ArrayList<>();
        Article article;
        while ((article = queue.poll()) != null) {
            futures.add(downloadAndSaveArticle(article));
        }
        futures.forEach(future -> {
            try {
                future.get();
            } catch (InterruptedException | ExecutionException e) {
                log.error("Error occurred while downloading or saving the article: " + e.getMessage());
                throw new ArticleProcessingException("Error occurred while downloading or saving the article.", e);
            }
        });
    }

    @Async
    public Future<Void> downloadAndSaveArticle(Article article) {
        return CompletableFuture.runAsync(() -> retryTemplate.execute(context -> {
            try {
                log.info("Downloading content for article with URL: " + article.getUrl());
                String content = downloadArticle(article.getUrl());
                article.setArticleContent(content);
                log.info("Saving article with title: " + article.getTitle());
                articleRepository.save(article);
                return null;
            } catch (Exception ex) {
                log.error("Error occurred while downloading or saving the article: " + ex.getMessage());
                throw new ArticleProcessingException("Error occurred while downloading or saving the article.", ex);
            }
        }), executorService);
    }

    private synchronized String downloadArticle(String url) {
        try {
            Document document = Jsoup.connect(url).get();
            return document.body().text();
        } catch (IOException e) {
            throw new ArticleProcessingException("Error fetching content from URL: " + url, e);
        }
    }
}