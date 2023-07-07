package com.articles.NewsDownloader.controller;

import com.articles.NewsDownloader.entity.Article;
import com.articles.NewsDownloader.repository.ArticleRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static com.articles.NewsDownloader.TestData.ARTICLE_MATCHER;
import static com.articles.NewsDownloader.TestData.articlesList;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ArticlesControllerTest extends AbstractControllerTest {

    static final String REST_URL = ArticleController.REST_URL;

    @Autowired
    private ArticleRepository repository;

    @Test
    void getAll() throws Exception {
        List<Article> articles = new ArrayList<>(articlesList);
        articles.sort(Comparator.comparing(Article::getPublishedAt));
        perform(MockMvcRequestBuilders.get(REST_URL))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(ARTICLE_MATCHER.contentJson(articles))
                .andDo(print());
    }
}
