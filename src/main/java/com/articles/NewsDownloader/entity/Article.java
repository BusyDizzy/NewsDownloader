package com.articles.NewsDownloader.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


import java.time.LocalDateTime;

@Entity
@Table(name = "ARTICLES")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Article {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "news_site", nullable = false)
    private String newsSiteName;

    private String url;

    @Column(name = "published_date", nullable = false)
    private LocalDateTime publishedAt;

    @Column(name = "article", columnDefinition = "TEXT")
    private String articleContent;
}

