package com.articles.NewsDownloader.controller;

import com.articles.NewsDownloader.dto.ArticleDTO;
import com.articles.NewsDownloader.model.Article;
import com.articles.NewsDownloader.repository.ArticleRepository;
import com.articles.NewsDownloader.service.DownloadService;
import com.articles.NewsDownloader.util.BlacklistUtil;
import lombok.extern.log4j.Log4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.util.List;

@Controller
@Log4j
public class DownloadController {

    @Value("${news-downloader.blacklist}")
    private String blacklist;

    @Value("${news-downloader.article-url}")
    private String articleUrl;
    private final ArticleRepository articleRepository;
    private final DownloadService downloadService;

    private final ModelMapper modelMapper;

    public DownloadController(ArticleRepository articleRepository, DownloadService downloadService, ModelMapper modelMapper) {
        this.articleRepository = articleRepository;
        this.downloadService = downloadService;
        this.modelMapper = modelMapper;
    }

    public List<ArticleDTO> downloadArticles() {
        List<String> blackList;
        try {
            blackList = BlacklistUtil.loadBlacklistedWords(blacklist);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        blackList.forEach(System.out::println);
        return downloadService.download(articleUrl);
    }

    public void saveArticles() {
        List<ArticleDTO> articleDTOList = downloadArticles();
        for (ArticleDTO articleDTO : articleDTOList) {
            Article article = modelMapper.map(articleDTO, Article.class);
            articleRepository.save(article);
        }
        log.info("Articles saved successfully.");
    }
}