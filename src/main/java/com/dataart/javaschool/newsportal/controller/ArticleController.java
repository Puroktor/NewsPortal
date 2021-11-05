package com.dataart.javaschool.newsportal.controller;

import com.dataart.javaschool.newsportal.service.ArticleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("api/article")
@RequiredArgsConstructor
public class ArticleController {
    private final ArticleService articleService;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadArticle(@RequestBody MultipartFile file) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(articleService.uploadArticle(file));
    }

    @GetMapping("/")
    public ResponseEntity<?> fetchAllArticles() {
        return ResponseEntity.ok(articleService.fetchAllArticles());
    }

    @GetMapping("/page")
    public ResponseEntity<?> fetchPage(@RequestParam("index") Integer index,
                                       @RequestParam("size") Integer size) {
        return ResponseEntity.ok(articleService.fetchPage(index, size));
    }
}
