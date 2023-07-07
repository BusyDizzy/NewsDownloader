package com.articles.NewsDownloader.service;

import com.articles.NewsDownloader.dto.ArticleDTO;

import java.util.List;

public interface NewsAPIFetcherService {
    List<ArticleDTO> fetchArticles(String url);
}