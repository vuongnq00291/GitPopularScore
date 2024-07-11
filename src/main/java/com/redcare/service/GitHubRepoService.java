package com.redcare.service;

import com.redcare.client.GitHubClient;
import com.redcare.domain.GitHubRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger LOGGER = LoggerFactory.getLogger(GitHubClient.class);
    private final GitHubClient gitHubClient;

    public GitHubRepoService(GitHubClient gitHubClient) {
        this.gitHubClient = gitHubClient;
    }

    /**
     * Fetches and scores repositories based on the specified programming language and creation date.
     * Calculate the popularity score.
     *
     * @param language     the programming language to filter repositories by (e.g., "java").
     * @param createdAfter the date (in YYYY-MM-DD format) to filter repositories created after this date.
     * @param perPage      the number of repositories to fetch per page. If null, the default value is used.
     * @return a list of {@link GitHubRepo} objects with calculated popularity scores.
     * @throws NullPointerException if the GitHub API response is missing expected fields.
     * @throws ClassCastException   if the data types in the GitHub API response are not as expected.
     */
    public List<GitHubRepo> getScoredRepositories(String language, String createdAfter, Integer perPage) {
        String query = "language:" + language + " created:>" + createdAfter;
        Map<String, Object> response = gitHubClient.fetchRepositories(query, "stars", "desc", perPage);
        List<Map<String, Object>> items = (List<Map<String, Object>>) response.get("items");
        List<GitHubRepo> scoredRepositories = new ArrayList<>();

        if (items != null) {
            for (Map<String, Object> repoData : items) {
                try {
                    GitHubRepo repo = new GitHubRepo();
                    repo.setName((String) repoData.get("name"));
                    repo.setStars((int) repoData.get("stargazers_count"));
                    repo.setForks((int) repoData.get("forks_count"));
                    repo.setUpdatedAt((String) repoData.get("updated_at"));
                    calculateScore(repo);
                    scoredRepositories.add(repo);
                } catch (NullPointerException | ClassCastException e) {
                    LOGGER.error("Error processing repository data: {}", e.getMessage());
                }
            }
        }
        return scoredRepositories;
    }

    public void calculateScore(GitHubRepo repo) {
        LocalDateTime now = LocalDateTime.now();
        try {
            OffsetDateTime offsetDateTime = OffsetDateTime.parse(repo.getUpdatedAt(), DateTimeFormatter.ISO_DATE_TIME);
            LocalDateTime updatedTime = offsetDateTime.toLocalDateTime();
            long daysSinceUpdate = ChronoUnit.DAYS.between(updatedTime, now);
            int score = (repo.getStars() * 3) + (repo.getForks() * 2) - (int) daysSinceUpdate;
            repo.setPopularityScore(Math.max(score, 0));
        } catch (Exception e) {
            LOGGER.error("Error parsing updated_at date: {}", e.getMessage());
            repo.setPopularityScore(0);
        }
    }
}
