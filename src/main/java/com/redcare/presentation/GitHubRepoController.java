package com.redcare.presentation;

import com.redcare.Exception.InvalidDateFormatException;
import com.redcare.Utils.DateUtils;
import com.redcare.config.MessageConfig;
import com.redcare.domain.GitHubRepo;
import com.redcare.service.GitHubRepoService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@ControllerAdvice
@RestController
@RequestMapping("/github")
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
     * @param limit     the number of repositories to return per page (optional)
     * @return a list of GitHub repositories matching the specified criteria
     * @throws InvalidDateFormatException if the createdAfter parameter is not in YYYY-MM-DD format
     */
    @GetMapping("/popularity-score")
    public List<GitHubRepo> getRepositories(@RequestParam(name = "language") String language,
                                            @RequestParam(name = "createdAfter") String createdAfter,
                                            @RequestParam(name = "limit", required = false) Integer limit) {

        if (!DateUtils.isValidDateFormat(createdAfter)) {
            throw new InvalidDateFormatException(message.getInvalidDateFormat());
        }
        return repositoryService.getScoredRepositories(language, createdAfter, limit);
    }


}
