package com.dataart.javaschool.newsportal.repository;

import com.dataart.javaschool.newsportal.entity.Article;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface ArticleRepository extends PagingAndSortingRepository<Article, Integer> {
    Page<Article> findAllByTheme(String theme, Pageable pageable);
}
