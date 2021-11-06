package com.dataart.javaschool.newsportal.controller;

import com.dataart.javaschool.newsportal.service.ArticleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("${article.articleControllerMapping}")
@RequiredArgsConstructor
public class ArticleController {
    private final ArticleService articleService;

    @PostMapping("${article.uploadArticleMapping}")
    public ResponseEntity<?> uploadArticle(@RequestBody MultipartFile file) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(articleService.uploadArticle(file));
    }

    @GetMapping("${article.fetchAllMapping}")
    public ResponseEntity<?> fetchAllArticles() {
        return ResponseEntity.ok(articleService.fetchAllArticles());
    }

    @GetMapping("${article.fetchPageMapping}")
    public ResponseEntity<?> fetchPage(@RequestParam("index") Integer index,
                                       @RequestParam("size") Integer size) {
        return ResponseEntity.ok(articleService.fetchPage(index, size));
    }
}
