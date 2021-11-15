package com.dataart.javaschool.newsportal;

import com.dataart.javaschool.newsportal.controller.dto.ArticleDto;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.File;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class NewsportalApplicationTests {
    public static final String baseURL = "http://127.0.0.1:7228";
    private final RestTemplate restTemplate;

    @Autowired
    public NewsportalApplicationTests(RestTemplateBuilder builder) {
        this.restTemplate = builder.build();
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    void testUploadArticle() {
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
    void testFetchPage() {
        String url = String.format("%s%s", baseURL, "/api/article");

        assertThrows(HttpClientErrorException.class, () ->
                restTemplate.getForEntity(url, Map.class));

        assertThrows(HttpClientErrorException.class, () ->
                restTemplate.getForEntity(
                        buildUriString(url, -1, 2, "Sport","id", true), Map.class));

        assertThrows(HttpClientErrorException.class, () ->
                restTemplate.getForEntity(
                        buildUriString(url, 1, -1, null, "id",false), Map.class));

        assertThrows(HttpClientErrorException.class, () ->
                restTemplate.getForEntity(
                        buildUriString(url, 1, 1, null, "aboba",false), Map.class));

        checkResponse(restTemplate.getForEntity(buildUriString(url, 0, 1, null,"id", false),
                Map.class));

        checkResponse(restTemplate.getForEntity(buildUriString(url, 0, 1, "Theme","id", false),
                Map.class));

        checkResponse(restTemplate.getForEntity(buildUriString(url, 0, 1, "Theme","creationTime", false),
                Map.class));
    }

    @SuppressWarnings("ConstantConditions, rawtypes, unchecked")
    private void checkResponse(ResponseEntity<Map> response) {
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(0, response.getBody().get("number"));
        Map<String, String> returnedArticle = (Map<String, String>) ((List<?>) response.getBody().get("content")).get(0);
        assertEquals("Title", returnedArticle.get("title"));
        assertEquals("Body", returnedArticle.get("body"));
        assertEquals("Theme", returnedArticle.get("theme"));
    }

    private ResponseEntity<ArticleDto> getFileAndPost(String location, String theme) {
        FileSystemResource resource = new FileSystemResource(new File(location));
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", resource);
        return restTemplate.postForEntity(buildUriString(
                        String.format("%s%s", baseURL, "/api/article"), theme),
                new HttpEntity<>(body, headers), ArticleDto.class);
    }

    private String buildUriString(String url, int index, int size, String theme, String sort, boolean isAsc) {
        String uri = UriComponentsBuilder
                .fromHttpUrl(url)
                .queryParam("index", index)
                .queryParam("size", size)
                .queryParam("sort", sort)
                .queryParam("isAsc", isAsc)
                .toUriString();
        if (theme != null) {
            return buildUriString(uri, theme);
        }
        return uri;
    }

    private String buildUriString(String url, String theme) {
        return UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("theme", theme).toUriString();
    }
}
