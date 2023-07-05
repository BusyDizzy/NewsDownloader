package com.articles.NewsDownloader.repository;

import com.articles.NewsDownloader.model.Article;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArticleRepository extends JpaRepository<Article, Long> {
}