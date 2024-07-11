package com.redcare.client;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Map;

@Service
public class GitHubClient {

    private final RestTemplate restTemplate;
    private final String githubApiUrl;
    private final Integer perPageDefault;

    public GitHubClient(@Value("${github.api.url}") String githubApiUrl,
                        @Value("${github.api.perPage}") Integer perPageDefault) {
        this.githubApiUrl = githubApiUrl;
        this.perPageDefault = perPageDefault;
        this.restTemplate = new RestTemplate();
    }

    public Map<String, Object> fetchRepositories(String query, String sort, String order, Integer perPage) {
        URI uri = UriComponentsBuilder.fromHttpUrl(githubApiUrl)
                .queryParam("q", query)
                .queryParam("sort", sort)
                .queryParam("order", order)
                .queryParam("per_page", perPage==null?perPageDefault:perPage)
                .build()
                .toUri();
        return restTemplate.getForObject(uri, Map.class);
    }
}
