package com.articles.NewsDownloader.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Objects;

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
    @JsonIgnore
    private String articleContent;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Article article = (Article) o;
        return Objects.equals(title, article.title) && Objects.equals(newsSiteName, article.newsSiteName) && Objects.equals(url, article.url) && Objects.equals(publishedAt, article.publishedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, newsSiteName, url, publishedAt);
    }

    @Override
    public String toString() {
        return "Article{" +
                "title='" + title + '\'' +
                ", newsSiteName='" + newsSiteName + '\'' +
                ", url='" + url + '\'' +
                ", publishedAt=" + publishedAt +
                '}';
    }
}

