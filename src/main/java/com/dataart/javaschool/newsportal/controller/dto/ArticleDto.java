package com.dataart.javaschool.newsportal.controller.dto;

import com.dataart.javaschool.newsportal.entity.ArticleEntity;
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
    private String text;
    private Timestamp creationTime;

    public ArticleDto(ArticleEntity entity) {
        this.title = entity.getTitle();
        this.text = entity.getText();
        this.creationTime =entity.getCreationTime();
    }
}
