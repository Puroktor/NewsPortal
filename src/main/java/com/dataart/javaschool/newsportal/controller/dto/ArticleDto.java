package com.dataart.javaschool.newsportal.controller.dto;

import com.dataart.javaschool.newsportal.entity.Article;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArticleDto {
    private Long id;
    private String title;
    private String body;
    private String theme;
    private Timestamp creationTime;

    public ArticleDto(Article entity) {
        this.title = entity.getTitle();
        this.body = entity.getBody();
        this.theme = entity.getTheme().substring(0, 1).toUpperCase() + entity.getTheme().substring(1);
        this.creationTime = entity.getCreationTime();
    }
}
