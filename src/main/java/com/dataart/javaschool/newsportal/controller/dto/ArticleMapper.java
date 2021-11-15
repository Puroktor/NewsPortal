package com.dataart.javaschool.newsportal.controller.dto;

import com.dataart.javaschool.newsportal.entity.Article;

public class ArticleMapper {
    public static ArticleDto toDto(Article article) {
        return ArticleDto.builder()
                .title(article.getTitle())
                .body(article.getBody())
                .theme(article.getTheme())
                .creationTime(article.getCreationTime())
                .build();
    }
}
