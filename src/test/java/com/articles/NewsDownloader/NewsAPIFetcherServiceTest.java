package com.articles.NewsDownloader;

import com.articles.NewsDownloader.dto.ArticleDTO;
import com.articles.NewsDownloader.exception.ArticleFetchingException;
import com.articles.NewsDownloader.service.NewsAPIFetcherService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;


@SpringBootTest
public class NewsAPIFetcherServiceTest {

    @InjectMocks
    private NewsAPIFetcherService newsAPIFetcherService;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private RetryTemplate retryTemplate;

    private final String validUrl = "https://api.spaceflightnewsapi.net/v3/articles";

    private final String invalidUrl = "https://invalid-url.com";

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }
    @Test
    public void testFetchArticlesWithValidURL() {
        // Arrange
        ArticleDTO articleDTO = new ArticleDTO();
        articleDTO.setTitle("Valid Article");
        List<ArticleDTO> expectedResponse = Collections.singletonList(articleDTO);

        ParameterizedTypeReference<List<ArticleDTO>> responseType = new ParameterizedTypeReference<>() {
        };

        ResponseEntity<List<ArticleDTO>> mockResponseEntity = new ResponseEntity<>(expectedResponse, HttpStatus.OK);
        when(restTemplate.exchange(
                eq(validUrl),
                eq(HttpMethod.GET),
                isNull(),
                eq(responseType)
        )).thenReturn(mockResponseEntity);

        when(retryTemplate.execute(any())).thenAnswer(invocation -> {
            RetryCallback callback = invocation.getArgument(0);
            return callback.doWithRetry(null);
        });

        // Act
        List<ArticleDTO> actualResponse = newsAPIFetcherService.fetchArticles(restTemplate, validUrl);

        // Assert
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    public void testFetchArticlesWithInvalidURL() {
        // Arrange
        ParameterizedTypeReference<List<ArticleDTO>> responseType = new ParameterizedTypeReference<>() {
        };

        when(restTemplate.exchange(
                eq(invalidUrl),
                eq(HttpMethod.GET),
                isNull(),
                eq(responseType)
        )).thenThrow(new RestClientException("Invalid URL"));

        when(retryTemplate.execute(any())).thenAnswer(invocation -> {
            RetryCallback callback = invocation.getArgument(0);
            return callback.doWithRetry(null);
        });

        // Act and Assert
        assertThrows(ArticleFetchingException.class, () -> {
            newsAPIFetcherService.fetchArticles(restTemplate, invalidUrl);
        });
    }
}
