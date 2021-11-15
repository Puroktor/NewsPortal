package com.dataart.javaschool.newsportal.service;

import com.dataart.javaschool.newsportal.controller.dto.ArticleDto;
import com.dataart.javaschool.newsportal.controller.dto.ArticleMapper;
import com.dataart.javaschool.newsportal.entity.Article;
import com.dataart.javaschool.newsportal.exception.TooBigFileException;
import com.dataart.javaschool.newsportal.exception.WrongFileFormatException;
import com.dataart.javaschool.newsportal.repository.ArticleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Service
@PropertySource("classpath:article.properties")
@RequiredArgsConstructor
public class ArticleService {

    private final ArticleRepository articleRepository;
    private final Validator validator;
    @Value("${article.required-filename}")
    private String requiredFileName;
    @Value("${article.max-size}")
    private Integer maxArticleSize;

    public ArticleDto uploadArticle(MultipartFile file, String theme) {
        if (maxArticleSize < file.getSize()) {
            throw new TooBigFileException("Article is too big! Max length - 20000 symbols");
        } else if (!Objects.requireNonNull(file.getOriginalFilename()).endsWith(".zip")) {
            throw new WrongFileFormatException("Not a .zip archive!");
        }
        try (ZipInputStream zipStream = new ZipInputStream(file.getInputStream())) {
            ZipEntry entry = zipStream.getNextEntry();
            if (entry == null) {
                throw new WrongFileFormatException("No file in the archive!");
            } else if (!entry.getName().equals(requiredFileName)) {
                throw new WrongFileFormatException("Invalid internal file name!");
            } else {
                BufferedReader reader = new BufferedReader(new InputStreamReader(zipStream));
                String title = reader.readLine();
                String body = reader.lines().collect(Collectors.joining());
                if (zipStream.getNextEntry() != null) {
                    throw new WrongFileFormatException("Multiple files in the archive!");
                }
                Article article = Article.builder()
                        .title(title)
                        .body(body)
                        .theme(theme)
                        .build();
                return saveArticle(article);
            }
        } catch (IOException e) {
            throw new WrongFileFormatException("Wrong format of the file!");
        }
    }

    private ArticleDto saveArticle(Article article) {
        Set<ConstraintViolation<Article>> violations = validator.validate(article);
        if (!violations.isEmpty()) {
            String exceptionMessage = violations
                    .stream()
                    .map(ConstraintViolation::getMessage)
                    .collect(Collectors.joining(", "));
            throw new WrongFileFormatException(exceptionMessage);
        }
        return ArticleMapper.toDto(articleRepository.save(article));
    }

    public Page<ArticleDto> fetchPageByTheme(String theme, int index, int size, String sortBy, boolean isAsc) {
        Sort sort = isAsc ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        return articleRepository
                .findAllByTheme(theme, PageRequest.of(index, size, sort))
                .map(ArticleMapper::toDto);
    }

    public Page<ArticleDto> fetchPage(int index, int size, String sortBy, boolean isAsc) {
        Sort sort = isAsc ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        return articleRepository
                .findAll(PageRequest.of(index, size, sort))
                .map(ArticleMapper::toDto);
    }
}
