package com.articles.NewsDownloader.service;

import com.articles.NewsDownloader.dto.ArticleDTO;

import java.util.List;

public interface DownloadService {
    List<ArticleDTO> NewsAPIFetcher(String url);

    String getPageContent(String url);
}