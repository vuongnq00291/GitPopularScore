package com.redcare.presentation;

import com.redcare.Exception.InvalidDateFormatException;
import com.redcare.Utils.DateUtils;
import com.redcare.config.MessageConfig;
import com.redcare.domain.GitHubRepo;
import com.redcare.service.GitHubRepoService;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@ControllerAdvice
@RestController
public class GitHubRepoController {

    private final GitHubRepoService repositoryService;
    private final MessageConfig message;
    public GitHubRepoController(GitHubRepoService repositoryService, MessageConfig message) {
        this.repositoryService = repositoryService;
        this.message = message;
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
            throw new InvalidDateFormatException(message.getInvalidDateFormat());
        }
        return repositoryService.getScoredRepositories(language, createdAfter, perPage);
    }


}
