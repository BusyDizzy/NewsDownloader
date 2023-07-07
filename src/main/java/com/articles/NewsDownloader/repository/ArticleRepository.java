package com.articles.NewsDownloader.repository;

import com.articles.NewsDownloader.entity.Article;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ArticleRepository extends JpaRepository<Article, Long> {
    List<Article> findByNewsSiteName(String newsSite);
}