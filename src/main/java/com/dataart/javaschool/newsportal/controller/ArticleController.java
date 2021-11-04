package com.dataart.javaschool.newsportal.controller;

import com.dataart.javaschool.newsportal.service.ArticleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/article")
@RequiredArgsConstructor
public class ArticleController {
    private final ArticleService articleService;

    @GetMapping("/")
    public ResponseEntity<?> fetchAllArticles() {
        return ResponseEntity.ok(articleService.fetchAllArticles());
    }
}
