package com.articles.NewsDownloader.exception;

public class ArticleProcessingException extends RuntimeException {
    public ArticleProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
}