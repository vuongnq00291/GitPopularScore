package com.redcare.service;

import com.redcare.client.GitHubClient;
import com.redcare.domain.GitHubRepo;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class GitHubRepoService {

    private final GitHubClient gitHubClient;


    public GitHubRepoService(GitHubClient gitHubClient) {
        this.gitHubClient = gitHubClient;
    }

    public List<GitHubRepo> getScoredRepositories(String language, String createdAfter, Integer perPage) {
        String query = "language:" + language + " created:>" + createdAfter;
        Map<String, Object> response = gitHubClient.fetchRepositories(query, "stars", "desc", perPage);
        List<Map<String, Object>> items = (List<Map<String, Object>>) response.get("items");
        List<GitHubRepo> scoredRepositories = new ArrayList<>();

        for (Map<String, Object> repoData : items) {
            GitHubRepo repo = new GitHubRepo();
            repo.setName((String) repoData.get("name"));
            repo.setStars((int) repoData.get("stargazers_count"));
            repo.setForks((int) repoData.get("forks_count"));
            repo.setUpdatedAt((String) repoData.get("updated_at"));
            calculateScore(repo);
            scoredRepositories.add(repo);
        }
        return scoredRepositories;
    }

    public void calculateScore(GitHubRepo repo) {
        LocalDateTime now = LocalDateTime.now();
        OffsetDateTime offsetDateTime = OffsetDateTime.parse(repo.getUpdatedAt(), DateTimeFormatter.ISO_DATE_TIME);
        LocalDateTime updatedTime = offsetDateTime.toLocalDateTime();
        long daysSinceUpdate = ChronoUnit.DAYS.between(updatedTime, now);
        int score = (repo.getStars()*3) + (repo.getForks() * 2) - (int) daysSinceUpdate;
        repo.setPopularityScore(Math.max(score, 0));
    }
}
