package com.articles.NewsDownloader.util;

import com.articles.NewsDownloader.dto.ArticleDTO;
import com.articles.NewsDownloader.model.Article;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ArticlesUtil {
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ISO_DATE_TIME;

    public static Article createNewFromDTO(ArticleDTO articleDTO) {
        return new Article(articleDTO.getId(),
                articleDTO.getTitle(),
                articleDTO.getNewsSite(),
                LocalDateTime.parse(articleDTO.getPublishedAt(), DATE_TIME_FORMATTER),
                null);
    }
}