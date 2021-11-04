package com.dataart.javaschool.newsportal.service;

import com.dataart.javaschool.newsportal.controller.dto.ArticleDto;
import com.dataart.javaschool.newsportal.repository.ArticleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ArticleService {

    private final ArticleRepository articleRepository;

    public List<ArticleDto> fetchAllArticles() {
        List<ArticleDto> dtoList = new ArrayList<>();
        articleRepository.findAll().forEach(article ->
                dtoList.add(new ArticleDto(article)));
        return dtoList;
    }
}
