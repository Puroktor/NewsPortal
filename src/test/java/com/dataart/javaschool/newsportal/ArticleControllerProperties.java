package com.dataart.javaschool.newsportal;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "article-controller")
public class ArticleControllerProperties {
    private String base;

    private String uploadArticle;

    private String fetchAll;

    private String fetchPage;

    private String fetchPageByTheme;
}
