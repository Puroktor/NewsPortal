package com.dataart.javaschool.newsportal.service;

import com.dataart.javaschool.newsportal.controller.dto.ArticleDto;
import com.dataart.javaschool.newsportal.entity.ArticleEntity;
import com.dataart.javaschool.newsportal.exception.WrongFileFormatException;
import com.dataart.javaschool.newsportal.repository.ArticleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

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

    public ArticleDto uploadArticle(MultipartFile file) {
        try (ZipInputStream zipStream = new ZipInputStream(file.getInputStream())) {
            ZipEntry entry = zipStream.getNextEntry();
            if (entry == null) {
                throw new WrongFileFormatException("No file in the archive!");
            } else if (!entry.getName().equals("article.txt")) {
                throw new WrongFileFormatException("Wrong inside file name!");
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
                ArticleEntity article = new ArticleEntity();
                article.setTitle(titleBuilder.toString());
                article.setText(textBuilder.toString());
                return new ArticleDto(articleRepository.save(article));
            }
        } catch (IOException e) {
            throw new WrongFileFormatException("Wrong format of the file!");
        }
    }
}
