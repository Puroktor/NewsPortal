package com.dataart.javaschool.newsportal.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ArticleEntity {
    @Id
    private Long id;
    private String title;
    private String text;
    private Timestamp creationTime = new Timestamp(System.currentTimeMillis());
}
