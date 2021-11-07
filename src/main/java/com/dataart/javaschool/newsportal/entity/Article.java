package com.dataart.javaschool.newsportal.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Article {
    @Id
    private Integer id;
    @NotNull
    @Size(min=1, max=50, message
            = "Title mist be be between 1 and 50 characters")
    private String title;
    @NotNull
    @Size(min=1, max=50, message
            = "Body mist be be between 1 and 20000 characters")
    private String body;
    @NotNull
    @Size(min=1, max=20, message
            = "Theme mist be be between 1 and 20 characters")
    private String theme;
    @Builder.Default
    private Timestamp creationTime = new Timestamp(System.currentTimeMillis());
}
