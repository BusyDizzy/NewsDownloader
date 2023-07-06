package com.articles.NewsDownloader.service;

import com.articles.NewsDownloader.dto.ArticleDTO;
import org.springframework.web.client.RestTemplate;

import java.util.List;

public interface NewsAPIFetcher {
    List<ArticleDTO> fetchArticles(RestTemplate restTemplate, String url);
}