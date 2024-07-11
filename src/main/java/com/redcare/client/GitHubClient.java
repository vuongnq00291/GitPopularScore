package com.redcare.client;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

@Service
public class GitHubClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(GitHubClient.class);
    private final RestTemplate restTemplate;
    private final String githubApiUrl;
    private final Integer perPageDefault;

    public GitHubClient(RestTemplate restTemplate,
                        @Value("${github.api.url}") String githubApiUrl,
                        @Value("${github.api.perPage}") Integer perPageDefault) {
        this.restTemplate = restTemplate;
        this.githubApiUrl = githubApiUrl;
        this.perPageDefault = perPageDefault;
    }

    /**
     * Fetches repositories from GitHub based on the specified query parameters.
     * return empty map if there is any issue from github server
     *
     * @param query    the search query to use for finding repositories.
     * @param sort     the field to sort results by (e.g., stars, forks).
     * @param order    the order to sort results in (asc for ascending, desc for descending).
     * @param perPage  the number of results per page. If null, a default value is used.
     * @return a map containing the response from GitHub's API.
     */
    public Map<String, Object> fetchRepositories(String query, String sort, String order, Integer perPage) {

        try{
            URI uri = UriComponentsBuilder.fromHttpUrl(githubApiUrl)
                    .queryParam("q", query)
                    .queryParam("sort", sort)
                    .queryParam("order", order)
                    .queryParam("per_page", perPage==null?perPageDefault:perPage)
                    .build()
                    .toUri();
            ResponseEntity<Map> responseEntity = restTemplate.getForEntity(uri, Map.class);
            HttpStatusCode statusCode = responseEntity.getStatusCode();
            if (statusCode.is2xxSuccessful()) {
                return responseEntity.getBody();
            } else {
                // Handle other unexpected statuses
                LOGGER.warn("GitHub Unexpected response status: {}", statusCode);
            }
        }catch (Exception e){
                //Handle github server error
                LOGGER.error("GitHub Unexpected error : ",e);
        }
        return new HashMap<>();
    }
}
