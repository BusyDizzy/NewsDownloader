package com.articles.NewsDownloader.service.impl;

import com.articles.NewsDownloader.component.BlacklistUtil;
import com.articles.NewsDownloader.dto.ArticleDTO;
import com.articles.NewsDownloader.entity.Article;
import com.articles.NewsDownloader.exception.ArticleProcessingException;
import com.articles.NewsDownloader.service.ArticleDownloadAndSaveService;
import com.articles.NewsDownloader.service.ArticlesProcessorService;
import com.articles.NewsDownloader.util.ArticlesUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

@Service
@Slf4j
public class ArticlesProcessorServiceImpl implements ArticlesProcessorService {
    private final List<String> blacklistedWords;
    @Value("${thread-pool.buffer-limit}")
    private int bufferLimit;  // Maximum number of articles in the buffer for each site
    private final ConcurrentHashMap<String, ConcurrentLinkedQueue<Article>> buffer = new ConcurrentHashMap<>();
    private final ArticleDownloadAndSaveService articleDownloadAndSaveService;

    public ArticlesProcessorServiceImpl(@Value("${news-downloader.blacklist}") Resource blacklistResource,
                                        ArticleDownloadAndSaveService articleDownloadAndSaveService,
                                        BlacklistUtil blacklistUtil) {
        this.articleDownloadAndSaveService = articleDownloadAndSaveService;
        try {
            this.blacklistedWords = blacklistUtil.loadBlacklistedWords(blacklistResource);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void processArticles(List<ArticleDTO> articleDTOs) {
        if (articleDTOs != null) {
            List<Article> articles = ArticlesUtil.convertToArticleList(articleDTOs);
            articles.removeIf(article -> isTitleBlacklisted(article.getTitle()));

            articles.sort(Comparator.comparing(Article::getPublishedAt));

            for (Article article : articles) {
                ConcurrentLinkedQueue<Article> queue = buffer.computeIfAbsent(article.getNewsSiteName(), k -> new ConcurrentLinkedQueue<>());
                queue.add(article);
                if (queue.size() >= bufferLimit) {
                    log.info("Starting download of the queue in buffer for site: {}", article.getNewsSiteName());
                    articleDownloadAndSaveService.downloadAndSaveArticles(queue);
                }
            }

            log.info("Save articles if anything left in buffer");
            for (ConcurrentLinkedQueue<Article> queue : buffer.values()) {
                articleDownloadAndSaveService.downloadAndSaveArticles(queue);
            }
        } else {
            log.info("Received an empty Articles list");
            throw new ArticleProcessingException("Error occurred while processing articles: no articles were downloaded");
        }
    }

    private boolean isTitleBlacklisted(String title) {
        return blacklistedWords.stream().anyMatch(title::contains);
    }
}