package com.articles.NewsDownloader.exception;

public class ArticleFetchingException extends RuntimeException {
    public ArticleFetchingException(String message, Throwable cause) {
        super(message, cause);
    }

    public ArticleFetchingException(String message) {
        super(message);
    }
}