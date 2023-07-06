package com.articles.NewsDownloader.controller;

import jakarta.annotation.PreDestroy;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
@Log4j
public class StartupRunner implements ApplicationRunner {

    private final ArticlesFetcher articlesFetcher;
    private final ExecutorService executorService;
    @Value("${thread-pool.count}")
    private int threadCount;

    public StartupRunner(ArticlesFetcher articlesFetcher,
                         @Value("${thread-pool.count}") int threadCount) {
        this.articlesFetcher = articlesFetcher;
        this.executorService = Executors.newFixedThreadPool(threadCount);
    }

    @Override
    public void run(ApplicationArguments args) {
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        for (int i = 0; i < threadCount; i++) {
            log.info("Thread " + i + " has started");
            futures.add(CompletableFuture.runAsync(articlesFetcher::fetchAndProcessArticles, executorService));
        }

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        // shut down the executor service so no more tasks can be submitted
        executorService.shutdown();
        log.info("Finishing run");
    }

    @PreDestroy
    public void shutdownExecutorService() {
        if (executorService != null) {
            log.info("Post destroy executor service shutdown");
            executorService.shutdown();
        }
    }
}
