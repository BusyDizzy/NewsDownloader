package com.articles.NewsDownloader;

import com.articles.NewsDownloader.controller.DownloadController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class NewsDownloaderApplication implements CommandLineRunner {
    private final DownloadController downloadController;

    @Autowired
    public NewsDownloaderApplication(DownloadController downloadController) {
        this.downloadController = downloadController;
    }

    public static void main(String[] args) {
        SpringApplication.run(NewsDownloaderApplication.class, args);
    }

    @Override
    public void run(String... args) {
        downloadController.saveArticles();
    }
}
