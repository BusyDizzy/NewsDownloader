package com.articles.NewsDownloader.service;

import com.articles.NewsDownloader.entity.Article;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Future;

public interface ArticleDownloadAndSaveService {
    void downloadAndSaveArticles(ConcurrentLinkedQueue<Article> queue);

    Future<Void> downloadAndSaveArticle(Article article);
}