package com.dataart.javaschool.newsportal.repository;

import com.dataart.javaschool.newsportal.entity.Article;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface ArticleRepository extends PagingAndSortingRepository<Article, Integer> {
    Iterable<Article> findAllByOrderByIdDesc();

    Page<Article> findAllByOrderByIdDesc(Pageable pageable);

    Page<Article> findAllByThemeOrderByIdDesc(String theme, Pageable pageable);
}
