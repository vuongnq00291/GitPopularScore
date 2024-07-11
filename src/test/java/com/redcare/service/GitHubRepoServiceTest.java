package com.redcare.service;

import com.redcare.client.GitHubClient;
import com.redcare.domain.GitHubRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class GitHubRepoServiceTest {

    @Mock
    private GitHubClient gitHubClient;

    @InjectMocks
    private GitHubRepoService gitHubRepoService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetScoredRepositories() {
        String language = "java";
        String createdAfter = "2023-01-01";
        Integer perPage = 10;

        Map<String, Object> repoData = new HashMap<>();
        repoData.put("name", "repo1");
        repoData.put("stargazers_count", 100);
        repoData.put("forks_count", 50);
        repoData.put("updated_at", "2023-07-10T12:34:56Z");

        Map<String, Object> response = new HashMap<>();
        response.put("items", List.of(repoData));

        when(gitHubClient.fetchRepositories("language:java created:>2023-01-01", "stars", "desc",10)).thenReturn(response);
        List<GitHubRepo> scoredRepositories = gitHubRepoService.getScoredRepositories(language, createdAfter, perPage);

        // Then
        assertEquals(1, scoredRepositories.size());
        GitHubRepo repo = scoredRepositories.get(0);
        assertEquals("repo1", repo.getName());
        assertEquals(100, repo.getStars());
        assertEquals(50, repo.getForks());
        assertTrue(repo.getPopularityScore() > 0);
    }

    @Test
    void testGetScoredRepositories_NullItems() {
        String language = "java";
        String createdAfter = "2023-01-01";
        Integer perPage = 10;

        Map<String, Object> response = new HashMap<>();
        response.put("items", null);
        when(gitHubClient.fetchRepositories("language:java created:>2023-01-01", "stars", "desc",10))
                .thenReturn(response);
        List<GitHubRepo> scoredRepositories = gitHubRepoService.getScoredRepositories(language, createdAfter, perPage);
        assertTrue(scoredRepositories.isEmpty());
    }

    @Test
    void testGetScoredRepositories_InvalidRepoData() {
        String language = "java";
        String createdAfter = "2023-01-01";
        Integer perPage = 10;

        Map<String, Object> repoData = new HashMap<>();
        repoData.put("name", "repo1");
        repoData.put("stargazers_count", "invalid"); // invalid data type
        repoData.put("forks_count", 50);
        repoData.put("updated_at", "2023-07-10T12:34:56Z");
        Map<String, Object> response = new HashMap<>();
        response.put("items", List.of(repoData));
        when(gitHubClient.fetchRepositories("language:java created:>2023-01-01", "stars", "desc",10))
                .thenReturn(response);
        List<GitHubRepo> scoredRepositories = gitHubRepoService.getScoredRepositories(language, createdAfter, perPage);
        assertTrue(scoredRepositories.isEmpty());
    }

    @Test
    void testCalculateScore_ValidDate() {
        GitHubRepo repo = new GitHubRepo();
        repo.setStars(100);
        repo.setForks(50);
        repo.setUpdatedAt("2023-07-10T12:34:56Z");
        gitHubRepoService.calculateScore(repo);
        assertTrue(repo.getPopularityScore() > 0);
    }

    @Test
    void testCalculateScore_InvalidDate() {
        // Given
        GitHubRepo repo = new GitHubRepo();
        repo.setStars(100);
        repo.setForks(50);
        repo.setUpdatedAt("invalid-date");
        gitHubRepoService.calculateScore(repo);
        assertEquals(0, repo.getPopularityScore());
    }
}
