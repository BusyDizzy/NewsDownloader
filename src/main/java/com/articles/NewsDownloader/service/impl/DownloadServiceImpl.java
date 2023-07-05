package com.articles.NewsDownloader.service.impl;

import com.articles.NewsDownloader.dto.ArticleDTO;
import com.articles.NewsDownloader.service.DownloadService;
import lombok.extern.log4j.Log4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Service
@Log4j
public class DownloadServiceImpl implements DownloadService {

    @Value("${news-downloader.articles.limit}")
    private int limit;

    @Value("${news-downloader.articles.skipped}")
    private int skipped;

    @Override
    public List<ArticleDTO> NewsAPIFetcher(String url) {
        RestTemplate restTemplate = new RestTemplate();
        url = url + "?_limit=" + limit + "&_start=" + skipped;
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

    @Override
    public String getPageContent(String url) {
        try {
            Document document = Jsoup.connect(url).get();
            return document.body().text();
        } catch (IOException e) {
            throw new RuntimeException("Error fetching content from URL: " + url, e);
        }
    }
}