package com.articles.NewsDownloader.controller;

import com.articles.NewsDownloader.entity.Article;
import com.articles.NewsDownloader.repository.ArticleRepository;
import lombok.extern.log4j.Log4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(value = ArticleController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
@Log4j
public class ArticleController {

    public static final String REST_URL = "/api/v1/articles";
    private final ArticleRepository articleRepository;

    public ArticleController(ArticleRepository articleRepository) {
        this.articleRepository = articleRepository;
    }

    @GetMapping
    public List<Article> getAllArticles() {
        List<Article> articleList = articleRepository.findAll();
        articleList.sort(Comparator.comparing(Article::getPublishedAt));
        return articleList;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Article> getArticleById(@PathVariable Long id) {
        Optional<Article> articleOptional = articleRepository.findById(id);
        return articleOptional.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/news-site/{newsSite}")
    public List<Article> getArticlesByNewsSite(@PathVariable String newsSite) {
        return articleRepository.findByNewsSiteName(newsSite);
    }
}