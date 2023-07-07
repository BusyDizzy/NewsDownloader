package com.articles.NewsDownloader.service.impl;

import com.articles.NewsDownloader.entity.Article;
import com.articles.NewsDownloader.exception.ArticleProcessingException;
import com.articles.NewsDownloader.repository.ArticleRepository;
import com.articles.NewsDownloader.service.ArticleDownloadAndSaveService;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class ArticleDownloadAndSaveServiceImpl implements ArticleDownloadAndSaveService {
    @Autowired
    private ExecutorService executorService;
    private final ArticleRepository articleRepository;

    @Autowired
    private RetryTemplate retryTemplate;

    public ArticleDownloadAndSaveServiceImpl(ArticleRepository articleRepository) {
        this.articleRepository = articleRepository;
    }

    @Override
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
                log.error("Error occurred while downloading or saving the article {}: ", e.getMessage());
                throw new ArticleProcessingException("Error occurred while downloading or saving the article.", e);
            }
        });
    }

    @Async
    @Override
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