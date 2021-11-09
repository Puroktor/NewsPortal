package com.dataart.javaschool.newsportal;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;

@Data
@ConfigurationProperties(prefix = "article-controller")
@PropertySource("classpath:article-controller.properties")
public class ArticleControllerProperties {
    private String base;

    private String uploadArticle;

    private String fetchAll;

    private String fetchPage;

    private String fetchPageByTheme;
}
