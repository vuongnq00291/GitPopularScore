package com.redcare.service;

import com.redcare.client.GitHubClient;
import com.redcare.domain.GitHubRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

public class GitHubRepoServiceTest {

    @Mock
    private GitHubClient gitHubClient;

    @InjectMocks
    private GitHubRepoService gitHubRepoService;

    @BeforeEach
    public void setUp() {
        openMocks(this);
    }

    @Test
    public void testGetScoredRepositories() {
        String language = "java";
        String createdAfter = "2023-01-01";
        Integer perPage = 10;

        OffsetDateTime now = OffsetDateTime.now();
        String updatedAt =   now.minusDays(10).format(DateTimeFormatter.ISO_DATE_TIME);

        Map<String, Object> mockResponse = Map.of(
                "items", List.of(
                        Map.of(
                                "name", "repo1",
                                "stargazers_count", 100,
                                "forks_count", 50,
                                "updated_at", updatedAt
                        ),
                        Map.of(
                                "name", "repo2",
                                "stargazers_count", 150,
                                "forks_count", 60,
                                "updated_at", updatedAt
                        )
                )
        );

        when(gitHubClient.fetchRepositories(anyString(), anyString(), anyString(), anyInt())).thenReturn(mockResponse);

        List<GitHubRepo> repos = gitHubRepoService.getScoredRepositories(language, createdAfter, perPage);

        assertEquals(2, repos.size());
        assertEquals("repo1", repos.get(0).getName());
        assertEquals("repo2", repos.get(1).getName());

        assertEquals(100, repos.get(0).getStars());
        assertEquals(50, repos.get(0).getForks());
        assertEquals(updatedAt, repos.get(0).getUpdatedAt());

        assertEquals(150, repos.get(1).getStars());
        assertEquals(60, repos.get(1).getForks());
        assertEquals(updatedAt, repos.get(1).getUpdatedAt());

        assertEquals(Math.max((100 * 3) + (50 * 2) - 10, 0), repos.get(0).getPopularityScore());
        assertEquals(Math.max((150 * 3) + (60 * 2) - 10, 0), repos.get(1).getPopularityScore());
    }
}
