package com.dataart.javaschool.newsportal.service;

import com.dataart.javaschool.newsportal.controller.dto.ArticleDto;
import com.dataart.javaschool.newsportal.entity.Article;
import com.dataart.javaschool.newsportal.exception.WrongFileFormatException;
import com.dataart.javaschool.newsportal.repository.ArticleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Service
@RequiredArgsConstructor
public class ArticleService {

    private final ArticleRepository articleRepository;
    @Value("${article.requiredFileName}")
    private String requiredFileName;

    public List<ArticleDto> fetchAllArticles() {
        List<ArticleDto> dtoList = new ArrayList<>();
        articleRepository.findAll().forEach(article ->
                dtoList.add(new ArticleDto(article)));
        return dtoList;
    }

    public ArticleDto uploadArticle(MultipartFile file) {
        if (file == null) {
            throw new WrongFileFormatException("File is missing!");
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
                StringBuilder titleBuilder = new StringBuilder();
                int ch;
                while ((ch = zipStream.read()) != '\n') {
                    if (ch == -1) {
                        throw new WrongFileFormatException("Article has no body!");
                    }
                    titleBuilder.append((char) ch);
                }
                StringBuilder textBuilder = new StringBuilder();
                while ((ch = zipStream.read()) != -1) {
                    textBuilder.append((char) ch);
                }
                if (zipStream.getNextEntry() != null) {
                    throw new WrongFileFormatException("Multiple files in the archive!");
                }
                Article article = Article.builder()
                        .title(titleBuilder.toString())
                        .text(textBuilder.toString())
                        .build();
                return new ArticleDto(articleRepository.save(article));
            }
        } catch (IOException e) {
            throw new WrongFileFormatException("Wrong format of the file!");
        }
    }
}
