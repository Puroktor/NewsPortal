package com.dataart.javaschool.newsportal;

import com.dataart.javaschool.newsportal.controller.dto.ArticleDto;
import com.dataart.javaschool.newsportal.entity.Article;
import com.dataart.javaschool.newsportal.repository.ArticleRepository;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.domain.*;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@SuppressWarnings("unchecked, rawtypes, ConstantConditions")
class NewsportalApplicationTests {

    @Value("${article.baseURL}")
    private String baseURL;
    @Value("${article.articleControllerMapping}")
    private String controllerURL;
    @Value("${article.uploadArticleMapping}")
    private String uploadURL;
    @Value("${article.fetchAllMapping}")
    private String fetchAllURL;
    @Value("${article.fetchPageMapping}")
    private String fetchPageURL;
    private final RestTemplate restTemplate;

    @MockBean
    private ArticleRepository mockRepository;

    @Autowired
    public NewsportalApplicationTests(RestTemplateBuilder builder) {
        this.restTemplate = builder.build();
    }

    @Test
    void testUploadArticle() {
        Mockito.when(mockRepository.save(Mockito.any(Article.class))).thenAnswer(i -> i.getArguments()[0]);
        String dir = "src/test/resources/%s";

        assertThrows(HttpClientErrorException.class, () -> getFileAndPost(String.format(dir, "test1.txt")));

        assertThrows(HttpClientErrorException.class, () -> getFileAndPost(String.format(dir, "test2.zip")));

        assertThrows(HttpClientErrorException.class, () -> getFileAndPost(String.format(dir, "test3.zip")));

        ResponseEntity<ArticleDto> responseEntity4 = getFileAndPost(String.format(dir, "test4.zip"));
        assertEquals(HttpStatus.CREATED, responseEntity4.getStatusCode());
        assertEquals("Title", responseEntity4.getBody().getTitle().replace("\r", ""));
        assertEquals("Body", responseEntity4.getBody().getBody());
    }

    @Test
    void testFetchAll() {
        List<Article> articles = new ArrayList<>();
        articles.add(Article.builder().title("1").body("2").build());
        articles.add(Article.builder().title("3").body("4").build());
        Mockito.when(mockRepository.findAll()).thenReturn(articles);
        ResponseEntity<List> response = restTemplate.getForEntity(
                String.format("%s%s%s", baseURL, controllerURL, fetchAllURL), List.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        for (int i = 0; i < 2; i++) {
            Map<String, String> returnedArticle = (Map<String, String>) response.getBody().get(i);
            assertEquals(articles.get(1 - i).getTitle(),
                    returnedArticle.get("title"));
            assertEquals(articles.get(1 - i).getBody(),
                    returnedArticle.get("body"));
        }
    }

    @Test
    void testFetchPage() {
        Article article1 = Article.builder().id(0).title("123").body("345").build();
        Article article2 = Article.builder().id(1).title("567").body("789").build();
        Mockito.when(mockRepository.findAll(Mockito.any(Pageable.class))).thenAnswer(i -> {
            Pageable pageable = i.getArgument(0);
            if (pageable.getPageNumber() == 0) {
                return new PageImpl<>(List.of(article2, article1));
            }
            return new PageImpl<>(Collections.EMPTY_LIST);
        });

        String url = String.format("%s%s%s", baseURL, controllerURL, fetchPageURL);

        assertThrows(HttpClientErrorException.class, () ->
                restTemplate.getForEntity(
                        buildUriString(url, 3, 2), Map.class));

        ResponseEntity<Map> response = restTemplate.getForEntity(
                buildUriString(url, 0, 1), Map.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Page<Article> page = mockRepository.findAll(PageRequest.of(0, 1, Sort.by("id").descending()));
        assertEquals(page.getTotalPages(), response.getBody().get("totalPages"));
        assertEquals(page.getNumber(), response.getBody().get("number"));
        Map<String, String> returnedArticle = (Map<String, String>) ((List<?>) response.getBody().get("content")).get(0);
        assertEquals(page.getContent().get(0).getTitle(),
                returnedArticle.get("title"));
        assertEquals(page.getContent().get(0).getBody(),
                returnedArticle.get("body"));
    }

    private ResponseEntity<ArticleDto> getFileAndPost(String location) {
        FileSystemResource resource = new FileSystemResource(new File(location));
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", resource);
        return restTemplate.postForEntity(String.format("%s%s%s", baseURL, controllerURL, uploadURL),
                new HttpEntity<>(body, headers), ArticleDto.class);
    }

    private String buildUriString(String url, int index, int size) {
        return UriComponentsBuilder
                .fromHttpUrl(url)
                .queryParam("index", index)
                .queryParam("size", size)
                .toUriString();
    }
}
