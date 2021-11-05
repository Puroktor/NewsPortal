package com.dataart.javaschool.newsportal.entity;

import lombok.*;
import org.springframework.data.annotation.Id;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Article {
    @Id
    private Integer id;
    private String title;
    private String text;
    @Builder.Default
    private Timestamp creationTime = new Timestamp(System.currentTimeMillis());
}
