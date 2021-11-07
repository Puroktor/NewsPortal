package com.dataart.javaschool.newsportal.service;

import com.dataart.javaschool.newsportal.controller.dto.ArticleDto;
import com.dataart.javaschool.newsportal.entity.Article;
import com.dataart.javaschool.newsportal.exception.EmptyPageException;
import com.dataart.javaschool.newsportal.exception.TooBigFileException;
import com.dataart.javaschool.newsportal.exception.WrongFileFormatException;
import com.dataart.javaschool.newsportal.repository.ArticleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Service
@RequiredArgsConstructor
public class ArticleService {

    private final ArticleRepository articleRepository;
    private final Validator validator;
    @Value("${article.requiredFileName}")
    private String requiredFileName;
    @Value("${article.maxArticleSize}")
    private Integer maxArticleSize;

    public List<ArticleDto> fetchAllArticles() {
        List<ArticleDto> dtoList = new ArrayList<>();
        articleRepository.findAllByOrderByIdDesc().forEach(article ->
                dtoList.add(new ArticleDto(article)));
        return dtoList;
    }

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
                String title = readTillChar(zipStream, '\n');
                String body = readTillChar(zipStream, -1);
                if (zipStream.getNextEntry() != null) {
                    throw new WrongFileFormatException("Multiple files in the archive!");
                }
                Article article = Article.builder()
                        .title(title)
                        .body(body)
                        .theme(theme.toLowerCase())
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
        return new ArticleDto(articleRepository.save(article));
    }

    private String readTillChar(InputStream stream, int till) throws IOException{
        StringBuilder stringBuilder = new StringBuilder();
        int ch;
        while ((ch = stream.read()) != till && ch != -1) {
            stringBuilder.append((char) ch);
        }
        return stringBuilder.toString();
    }

    public Page<ArticleDto> fetchPage(int index, int size) {
        Page<ArticleDto> dtoPage = articleRepository
                .findAllByOrderByIdDesc(PageRequest.of(index, size))
                .map(ArticleDto::new);
        if (dtoPage.isEmpty()) {
            throw new EmptyPageException("This page is empty!");
        }
        return dtoPage;
    }

    public Page<ArticleDto> fetchPageByTheme(String theme, int index, int size) {
        Page<ArticleDto> dtoPage = articleRepository
                .findAllByThemeOrderByIdDesc(theme.toLowerCase(), PageRequest.of(index, size))
                .map(ArticleDto::new);
        if (dtoPage.isEmpty()) {
            throw new EmptyPageException("This page is empty!");
        }
        return dtoPage;
    }
}
