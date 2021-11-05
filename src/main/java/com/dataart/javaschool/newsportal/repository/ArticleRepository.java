package com.dataart.javaschool.newsportal.repository;

import com.dataart.javaschool.newsportal.entity.Article;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface ArticleRepository extends PagingAndSortingRepository<Article, Long> {
}
