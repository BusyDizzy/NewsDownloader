package com.articles.NewsDownloader.repository;

import com.articles.NewsDownloader.entity.Article;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArticleRepository extends JpaRepository<Article, Long> {
}