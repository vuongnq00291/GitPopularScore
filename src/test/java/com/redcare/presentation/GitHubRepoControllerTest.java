package com.redcare.presentation;

import com.redcare.domain.GitHubRepo;
import com.redcare.service.GitHubRepoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@WebMvcTest(GitHubRepoController.class)
public class GitHubRepoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GitHubRepoService gitHubRepoService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testMissingLanguageParam_thenReturns400() throws Exception {
        mockMvc.perform(get("/repositories")
                        .param("createdAfter", "2023-01-01"))
                .andExpect(status().isBadRequest())
                .andExpect(content().json("{\"error\":\"language parameter is missing\"}"));
    }

    @Test
    public void testMissingCreatedAfterParam_thenReturns400() throws Exception {
        mockMvc.perform(get("/repositories")
                        .param("language", "Java"))
                .andExpect(status().isBadRequest())
                .andExpect(content().json("{\"error\":\"createdAfter parameter is missing\"}"));
    }

    @Test
    public void testInvalidDateFormat_thenReturns400() throws Exception {
        mockMvc.perform(get("/repositories")
                        .param("language", "Java")
                        .param("createdAfter", "01-01-2023"))
                .andExpect(status().isBadRequest())
                .andExpect(content().json("{\"error\":\"createdAfter parameter must be in YYYY-MM-DD format\"}"));
    }

    @Test
    public void testGetRepositories() throws Exception {
        GitHubRepo repo1 = new GitHubRepo();
        repo1.setName("repo1");
        repo1.setStars(100);
        repo1.setForks(50);
        repo1.setUpdatedAt("2023-07-01T00:00:00");
        repo1.setPopularityScore(400);

        GitHubRepo repo2 = new GitHubRepo();
        repo2.setName("repo2");
        repo2.setStars(150);
        repo2.setForks(60);
        repo2.setUpdatedAt("2023-07-01T00:00:00");
        repo2.setPopularityScore(500);

        when(gitHubRepoService.getScoredRepositories("java","2023-01-01",10)).thenReturn(List.of(repo1, repo2));

        mockMvc.perform(get("/repositories")
                        .param("language", "java")
                        .param("createdAfter", "2023-01-01")
                        .param("perPage", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name", is("repo1")))
                .andExpect(jsonPath("$[0].stars", is(100)))
                .andExpect(jsonPath("$[0].forks", is(50)))
                .andExpect(jsonPath("$[0].updatedAt", is("2023-07-01T00:00:00")))
                .andExpect(jsonPath("$[0].popularityScore", is(400)))
                .andExpect(jsonPath("$[1].name", is("repo2")))
                .andExpect(jsonPath("$[1].stars", is(150)))
                .andExpect(jsonPath("$[1].forks", is(60)))
                .andExpect(jsonPath("$[1].updatedAt", is("2023-07-01T00:00:00")))
                .andExpect(jsonPath("$[1].popularityScore", is(500)));
    }
}
