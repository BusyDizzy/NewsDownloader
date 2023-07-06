package com.articles.NewsDownloader.service.impl;

import com.articles.NewsDownloader.dto.ArticleDTO;
import com.articles.NewsDownloader.exception.ArticleFetchingException;
import com.articles.NewsDownloader.service.NewsAPIFetcher;
import lombok.extern.log4j.Log4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
@Log4j
public class NewsAPIFetcherImpl implements NewsAPIFetcher {

    @Retryable(maxAttemptsExpression = "${news-downloader.content-download-repeat-attempts}",
            backoff = @Backoff(delayExpression = "${news-downloader.retry-backoff-delay}"))
    @Override
    public List<ArticleDTO> fetchArticles(RestTemplate restTemplate, String url) {
        ResponseEntity<List<ArticleDTO>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );
        if (response.getBody() == null) {
            throw new ArticleFetchingException("No articles fetched from URL: " + url);
        }
        log.info("Fetching is finished for articles from URL: " + url);
        return response.getBody();
    }
}
