package com.articles.NewsDownloader.service.impl;

import com.articles.NewsDownloader.dto.ArticleDTO;
import com.articles.NewsDownloader.exception.ArticleFetchingException;
import com.articles.NewsDownloader.service.ArticlesFetcherService;
import com.articles.NewsDownloader.service.ArticlesProcessorService;
import com.articles.NewsDownloader.service.NewsAPIFetcher;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Log4j
public class ArticlesFetcherServiceImpl implements ArticlesFetcherService {

    private final NewsAPIFetcher newsAPIFetcher;
    private final ArticlesProcessorService articlesProcessorService;
    private final RestTemplate restTemplate;
    private final AtomicInteger offset = new AtomicInteger(0);
    @Value("${news-downloader.articles.limit}")
    private int articlesPerThreadLimit;
    @Value("${news-downloader.articles.total}")
    private int totalRecordsToDownload;
    @Value("${news-downloader.article-url}")
    private String articlesAPIUrl;

    public ArticlesFetcherServiceImpl(NewsAPIFetcher newsAPIFetcher, ArticlesProcessorService articlesProcessorService,
                                      RestTemplate restTemplate) {
        this.newsAPIFetcher = newsAPIFetcher;
        this.articlesProcessorService = articlesProcessorService;
        this.restTemplate = restTemplate;
    }

    @Override
    public void fetchAndProcessArticles() {
        int currentOffset;
        while ((currentOffset = offset.getAndAdd(articlesPerThreadLimit)) < totalRecordsToDownload) {
            String apiUrl = String.format("%s?_limit=%d&_start=%d", articlesAPIUrl, articlesPerThreadLimit, currentOffset);

            try {
                List<ArticleDTO> articleDTOs = newsAPIFetcher.fetchArticles(restTemplate, apiUrl);
                articlesProcessorService.processArticles(articleDTOs);
            } catch (Exception e) {
                throw new ArticleFetchingException("Error while fetching articles.", e);
            }
        }
    }
}
