package com.articles.NewsDownloader.component;

import com.articles.NewsDownloader.service.ArticlesFetcherService;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class StartupRunner implements ApplicationRunner {

    private final ArticlesFetcherService articlesFetcherService;
    private final ExecutorService executorService;
    @Value("${thread-pool.count}")
    private int threadCount;

    public StartupRunner(ArticlesFetcherService articlesFetcherService,
                         @Value("${thread-pool.count}") int threadCount) {
        this.articlesFetcherService = articlesFetcherService;
        this.executorService = Executors.newFixedThreadPool(threadCount);
    }

    @Override
    public void run(ApplicationArguments args) {
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        for (int i = 0; i < threadCount; i++) {
            log.info("Thread {} has started", i);
            futures.add(CompletableFuture.runAsync(articlesFetcherService::fetchAndProcessArticles, executorService));
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
