package com.articles.NewsDownloader.service.impl;

import com.articles.NewsDownloader.dto.ArticleDTO;
import com.articles.NewsDownloader.service.NewsAPIFetcher;
import lombok.extern.log4j.Log4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;

@Service
@Log4j
public class NewsAPIFetcherImpl implements NewsAPIFetcher {

    @Override
    public List<ArticleDTO> fetchArticles(String url) {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<List<ArticleDTO>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );
        if (response.getBody() == null) {
            return Collections.emptyList();
        }
        return response.getBody();
    }
}