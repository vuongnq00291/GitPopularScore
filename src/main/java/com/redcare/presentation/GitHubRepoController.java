package com.redcare.presentation;

import com.redcare.Exception.InvalidDateFormatException;
import com.redcare.Utils.DateUtils;
import com.redcare.service.GitHubRepoService;
import com.redcare.domain.GitHubRepo;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

@ControllerAdvice
@RestController
public class GitHubRepoController {

    private final GitHubRepoService repositoryService;

    public GitHubRepoController(GitHubRepoService repositoryService) {
        this.repositoryService = repositoryService;
    }

    /**
     * Retrieves a list of GitHub repositories Including popularity score
     * filtered by programming language and creation date.
     * @param language    the programming language to filter repositories by (e.g., "java", "python")
     * @param createdAfter the date to filter repositories created after, in YYYY-MM-DD format
     * @param perPage     the number of repositories to return per page (optional)
     * @return a list of GitHub repositories matching the specified criteria
     * @throws InvalidDateFormatException if the createdAfter parameter is not in YYYY-MM-DD format
     */
    @GetMapping("/repositories")
    public List<GitHubRepo> getRepositories(@RequestParam(name = "language") String language,
                                            @RequestParam(name = "createdAfter") String createdAfter,
                                            @RequestParam(name = "perPage", required = false) Integer perPage) {

        if (!DateUtils.isValidDateFormat(createdAfter)) {
            throw new InvalidDateFormatException("createdAfter parameter must be in YYYY-MM-DD format");
        }
        return repositoryService.getScoredRepositories(language, createdAfter, perPage);
    }


}
