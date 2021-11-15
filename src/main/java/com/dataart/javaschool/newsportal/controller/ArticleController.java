package com.dataart.javaschool.newsportal.controller;

import com.dataart.javaschool.newsportal.service.ArticleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Validated
public class ArticleController {
    private final ArticleService articleService;

    @PostMapping("/article")
    public ResponseEntity<?> uploadArticle(@NotNull @RequestBody MultipartFile file,
                                           @RequestParam("theme") String theme /* validated in service layer*/) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(articleService.uploadArticle(file, theme));
    }

    @GetMapping("/article")
    public ResponseEntity<?> fetchPage(@Size(min = 1, max = 20) @RequestParam(value = "theme", required = false) String theme,
                                       @Min(0) @RequestParam("index") int index,
                                       @Min(1) @RequestParam("size") int size,
                                       @Pattern(regexp = "id|title|body|creationTime") @RequestParam("sort") String sortBy,
                                       @RequestParam("isAsc") boolean isAsc) {
        if (theme != null) {
            return ResponseEntity.ok(articleService.fetchPageByTheme(theme, index, size, sortBy, isAsc));
        } else {
            return ResponseEntity.ok(articleService.fetchPage(index, size, sortBy, isAsc));
        }
    }
}
