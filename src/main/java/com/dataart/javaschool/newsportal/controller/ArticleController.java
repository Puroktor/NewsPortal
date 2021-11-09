package com.dataart.javaschool.newsportal.controller;

import com.dataart.javaschool.newsportal.service.ArticleService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@RestController
@PropertySource("classpath:article-controller.properties")
@RequestMapping("${article-controller.base}")
@RequiredArgsConstructor
@Validated
public class ArticleController {
    private final ArticleService articleService;

    @PostMapping("${article-controller.upload-article}")
    public ResponseEntity<?> uploadArticle(@NotNull @RequestBody MultipartFile file,
                                           @RequestParam("theme") String theme /* validated in service layer*/) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(articleService.uploadArticle(file, theme));
    }

    @GetMapping("${article-controller.fetch-all}")
    public ResponseEntity<?> fetchAllArticles() {
        return ResponseEntity.ok(articleService.fetchAllArticles());
    }

    @GetMapping("${article-controller.fetch-page}")
    public ResponseEntity<?> fetchPage(@Min(0) @RequestParam("index") int index,
                                       @Min(1) @RequestParam("size") int size) {
        return ResponseEntity.ok(articleService.fetchPage(index, size));
    }

    @GetMapping("${article-controller.fetch-page-by-theme}")
    public ResponseEntity<?> fetchPageByTheme(@NotNull @Size(min = 1, max = 20) String theme,
                                       @Min(0) @RequestParam("index") int index,
                                       @Min(1) @RequestParam("size") int size) {
        return ResponseEntity.ok(articleService.fetchPageByTheme(theme, index, size));
    }
}
