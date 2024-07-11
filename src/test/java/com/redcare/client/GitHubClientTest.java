package com.redcare.client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class GitHubClientTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private GitHubClient gitHubClient;

    private String githubApiUrl = "https://api.github.com/search/repositories";
    private Integer perPageDefault = 30;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        gitHubClient = new GitHubClient(restTemplate, githubApiUrl, perPageDefault);
    }

    @Test
    void testFetchRepositories_Success() {
        String query = "test";
        String sort = "stars";
        String order = "desc";
        Integer perPage = null;
        Map<String, Object> expectedResponse = new HashMap<>();
        expectedResponse.put("total_count", 1);
        expectedResponse.put("items", new HashMap[] { new HashMap<>() });

        URI uri = URI.create(githubApiUrl + "?q=test&sort=stars&order=desc&per_page=30");
        ResponseEntity<Map> responseEntity = new ResponseEntity<>(expectedResponse, HttpStatus.OK);
        when(restTemplate.getForEntity(uri, Map.class)).thenReturn(responseEntity);
        Map<String, Object> actualResponse = gitHubClient.fetchRepositories(query, sort, order, perPage);
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    void testFetchRepositories_Failure() {
        // Given
        String query = "test";
        String sort = "stars";
        String order = "desc";
        Integer perPage = null; // to use default

        URI uri = URI.create(githubApiUrl + "?q=test&sort=stars&order=desc&per_page=30");
        ResponseEntity<Map> responseEntity = new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);

        when(restTemplate.getForEntity(uri, Map.class)).thenReturn(responseEntity);
        Map<String, Object> actualResponse = gitHubClient.fetchRepositories(query, sort, order, perPage);
        assertEquals(new HashMap<>(), actualResponse);
    }

    @Test
    void testFetchRepositories_Exception() {
        String query = "test";
        String sort = "stars";
        String order = "desc";
        Integer perPage = null;

        URI uri = URI.create(githubApiUrl + "?q=test&sort=stars&order=desc&per_page=30");
        when(restTemplate.getForEntity(uri, Map.class)).thenThrow(new RuntimeException("Exception"));
        Map<String, Object> actualResponse = gitHubClient.fetchRepositories(query, sort, order, perPage);
        assertEquals(new HashMap<>(), actualResponse);
    }
}
