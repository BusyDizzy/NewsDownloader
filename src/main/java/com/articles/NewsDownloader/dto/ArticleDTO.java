package com.articles.NewsDownloader.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ArticleDTO {
    private Long id;

    private boolean featured = false;

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "URL is required")
    private String url;

    @NotBlank(message = "Image URL is required")
    private String imageUrl;

    @NotBlank(message = "News site is required")
    private String newsSite;

    @NotBlank(message = "Summary is required")
    private String summary;

    @NotBlank(message = "Published at is required")
    private String publishedAt;

    private List<Launch> launches;

    private List<String> events;
}