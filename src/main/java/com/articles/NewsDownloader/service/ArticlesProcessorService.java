package com.articles.NewsDownloader.service;

import com.articles.NewsDownloader.dto.ArticleDTO;

import java.util.List;

public interface ArticlesProcessorService {
    void processArticles(List<ArticleDTO> articleDTOs);
}