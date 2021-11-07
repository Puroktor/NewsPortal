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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.lang.Nullable;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
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
    @Value("${article.fetchPageByThemeMapping}")
    private String fetchPageByThemeMapping;
    private final RestTemplate restTemplate;

    @MockBean
    private ArticleRepository mockRepository;

    @Autowired
    public NewsportalApplicationTests(RestTemplateBuilder builder) {
        this.restTemplate = builder.build();
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    void testUploadArticle() {
        Mockito.when(mockRepository.save(Mockito.any(Article.class))).thenAnswer(i -> i.getArguments()[0]);
        String dir = "src/test/resources/%s";

        assertThrows(HttpClientErrorException.class, () ->
                getFileAndPost(String.format(dir, "test1.txt"), "Theme"));

        assertThrows(HttpClientErrorException.class, () ->
                getFileAndPost(String.format(dir, "test2.zip"), "Theme"));

        assertThrows(HttpClientErrorException.class, () ->
                getFileAndPost(String.format(dir, "test3.zip"), "Theme"));

        assertThrows(HttpClientErrorException.class, () ->
                getFileAndPost(String.format(dir, "test4.zip"), null));

        ResponseEntity<ArticleDto> responseEntity4 = getFileAndPost(String.format(dir, "test4.zip"), "Theme");
        assertEquals(HttpStatus.CREATED, responseEntity4.getStatusCode());
        assertEquals("Title", responseEntity4.getBody().getTitle().replace("\r", ""));
        assertEquals("Body", responseEntity4.getBody().getBody());
        assertEquals("Theme", responseEntity4.getBody().getTheme());
    }

    @Test
    @SuppressWarnings("unchecked, rawtypes, ConstantConditions")
    void testFetchAll() {
        List<Article> articles = new ArrayList<>();
        articles.add(Article.builder().title("3").body("4").theme("finance").build());
        articles.add(Article.builder().title("1").body("2").theme("sport").build());
        List<ArticleDto> dtoList = articles.stream().map(ArticleDto::new).collect(Collectors.toList());
        Mockito.when(mockRepository.findAllByOrderByIdDesc()).thenReturn(articles);
        ResponseEntity<List> response = restTemplate.getForEntity(
                String.format("%s%s%s", baseURL, controllerURL, fetchAllURL), List.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        for (int i = 0; i < 2; i++) {
            Map<String, String> returnedArticle = (Map<String, String>) response.getBody().get(i);
            assertEquals(dtoList.get(i).getTitle(),
                    returnedArticle.get("title"));
            assertEquals(dtoList.get(i).getBody(),
                    returnedArticle.get("body"));
            assertEquals(dtoList.get(i).getTheme(),
                    returnedArticle.get("theme"));
        }
    }

    @Test
    void testFetchPage() {
        Article article = Article.builder().id(0).title("123").body("345").theme("sport").build();
        Page<Article> page = new PageImpl<>(List.of(article));
        Mockito.when(mockRepository.findAllByOrderByIdDesc(Mockito.any(Pageable.class))).thenAnswer(i -> {
            Pageable p = i.getArgument(0);
            if (p.getPageNumber() == 0 && p.getPageSize() == 1) {
                return page;
            }
            return new PageImpl<>(new ArrayList<>());
        });

        String url = String.format("%s%s%s", baseURL, controllerURL, fetchPageURL);
        testPage(url, page, null);
    }

    @SuppressWarnings("unchecked, rawtypes, ConstantConditions")
    private void testPage(String url, Page<Article> page, @Nullable String theme) {
        ArticleDto dto = new ArticleDto(page.getContent().get(0));
        assertThrows(HttpClientErrorException.class, () ->
                restTemplate.getForEntity(url, Map.class));

        assertThrows(HttpClientErrorException.class, () ->
                restTemplate.getForEntity(
                        buildUriString(url, 3, 2, theme), Map.class));

        assertThrows(HttpClientErrorException.class, () ->
                restTemplate.getForEntity(
                        buildUriString(url, -1, 2, theme), Map.class));

        ResponseEntity<Map> response = restTemplate.getForEntity(
                buildUriString(url, 0, 1, theme), Map.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(page.getTotalPages(), response.getBody().get("totalPages"));
        assertEquals(page.getNumber(), response.getBody().get("number"));
        Map<String, String> returnedArticle = (Map<String, String>) ((List<?>) response.getBody().get("content")).get(0);
        assertEquals(dto.getTitle(), returnedArticle.get("title"));
        assertEquals(dto.getBody(), returnedArticle.get("body"));
        assertEquals(dto.getTheme(), returnedArticle.get("theme"));
    }

    @Test
    void testFetchPageByTheme() {
        Article article = Article.builder().id(0).title("123").body("345").theme("sport").build();
        Page<Article> page = new PageImpl<>(List.of(article));
        Mockito.when(mockRepository.findAllByThemeOrderByIdDesc(Mockito.any(String.class), Mockito.any(Pageable.class)))
                .thenAnswer(i -> {
                    String theme = i.getArgument(0);
                    Pageable p = i.getArgument(1);
                    if (p.getPageNumber() == 0 && p.getPageSize() == 1 && theme.equals("sport")) {
                        return page;
                    }
                    return new PageImpl<>(new ArrayList<>());
                });

        String url = String.format("%s%s%s", baseURL, controllerURL, fetchPageByThemeMapping);
        testPage(url, page, "Sport");
    }

    private ResponseEntity<ArticleDto> getFileAndPost(String location, String theme) {
        FileSystemResource resource = new FileSystemResource(new File(location));
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", resource);
        return restTemplate.postForEntity(buildUriString(
                        String.format("%s%s%s", baseURL, controllerURL, uploadURL), theme),
                new HttpEntity<>(body, headers), ArticleDto.class);
    }

    private String buildUriString(String url, int index, int size) {
        return UriComponentsBuilder
                .fromHttpUrl(url)
                .queryParam("index", index)
                .queryParam("size", size)
                .toUriString();
    }

    private String buildUriString(String url, int index, int size, String theme) {
        return buildUriString(buildUriString(url, index, size), theme);
    }

    private String buildUriString(String url, String theme) {
        if (theme == null) {
            return url;
        }
        return UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("theme", theme).toUriString();
    }
}
