package com.dataart.javaschool.newsportal.repository;

import com.dataart.javaschool.newsportal.entity.ArticleEntity;
import org.springframework.data.repository.CrudRepository;

public interface ArticleRepository extends CrudRepository<ArticleEntity, Long> {
}
