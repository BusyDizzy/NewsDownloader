package com.articles.NewsDownloader.controller;

import com.articles.NewsDownloader.entity.Article;
import com.articles.NewsDownloader.repository.ArticleRepository;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = ArticleController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
@Tag(name = "Articles REST API Controller")
public class ArticleController {

    public static final String REST_URL = "/api/v1/articles";
    private final ArticleRepository articleRepository;

    public ArticleController(ArticleRepository articleRepository) {
        this.articleRepository = articleRepository;
    }

    @GetMapping
    public List<Article> getAllArticles() {
        log.info("Extracting list of articles from database");
        return articleRepository.findAll()
                .stream()
                .sorted(Comparator.comparing(Article::getPublishedAt))
                .collect(Collectors.toList());

    }

    @GetMapping("/{id}")
    public ResponseEntity<Article> getArticleById(@PathVariable Long id) {
        log.info("Accessing single article from database with id {}: ", id);
        Optional<Article> articleOptional = articleRepository.findById(id);
        return articleOptional.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/news-site/{newsSite}")
    public List<Article> getArticlesByNewsSite(@PathVariable String newsSite) {
        log.info("Extracting list of articles from database for the website {}: ", newsSite);
        return articleRepository.findByNewsSiteName(newsSite)
                .stream()
                .sorted(Comparator.comparing(Article::getPublishedAt))
                .collect(Collectors.toList());
    }
}