package com.articles.NewsDownloader.service.impl;

import com.articles.NewsDownloader.dto.ArticleDTO;
import com.articles.NewsDownloader.exception.ArticleFetchingException;
import com.articles.NewsDownloader.service.ArticlesFetcherService;
import com.articles.NewsDownloader.service.ArticlesProcessorService;
import com.articles.NewsDownloader.service.NewsAPIFetcherService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Slf4j
public class ArticlesFetcherServiceImpl implements ArticlesFetcherService {

    private final NewsAPIFetcherService newsAPIFetcherService;
    private final ArticlesProcessorService articlesProcessorService;
    private final AtomicInteger offset = new AtomicInteger(0);
    @Value("${news-downloader.articles.limit}")
    private int articlesPerThreadLimit;
    @Value("${news-downloader.articles.total}")
    private int totalRecordsToDownload;
    @Value("${news-downloader.article-url}")
    private String articlesAPIUrl;

    public ArticlesFetcherServiceImpl(NewsAPIFetcherService newsAPIFetcherService,
                                      ArticlesProcessorService articlesProcessorService) {
        this.newsAPIFetcherService = newsAPIFetcherService;
        this.articlesProcessorService = articlesProcessorService;
    }

    @Override
    public void fetchAndProcessArticles() {
        int currentOffset;
        while ((currentOffset = offset.getAndAdd(articlesPerThreadLimit)) < totalRecordsToDownload) {
            String apiUrl = String.format("%s?_limit=%d&_start=%d", articlesAPIUrl, articlesPerThreadLimit, currentOffset);

            try {
                List<ArticleDTO> articleDTOs = newsAPIFetcherService.fetchArticles(apiUrl);
                articlesProcessorService.processArticles(articleDTOs);
            } catch (Exception e) {
                throw new ArticleFetchingException("Error while fetching articles.", e);
            }
        }
    }
}
