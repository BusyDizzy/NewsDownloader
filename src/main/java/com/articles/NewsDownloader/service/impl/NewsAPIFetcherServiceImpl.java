package com.articles.NewsDownloader.service.impl;

import com.articles.NewsDownloader.dto.ArticleDTO;
import com.articles.NewsDownloader.exception.ArticleFetchingException;
import com.articles.NewsDownloader.service.NewsAPIFetcherService;
import lombok.extern.log4j.Log4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
@Log4j
public class NewsAPIFetcherServiceImpl implements NewsAPIFetcherService {

    private final RetryTemplate retryTemplate;

    public NewsAPIFetcherServiceImpl(RetryTemplate retryTemplate) {
        this.retryTemplate = retryTemplate;
    }

    @Override
    public List<ArticleDTO> fetchArticles(RestTemplate restTemplate, String url) {
        return retryTemplate.execute(context -> {
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
        });
    }
}
