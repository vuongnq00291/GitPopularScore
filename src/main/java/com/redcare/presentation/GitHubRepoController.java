package com.redcare.presentation;

import com.redcare.service.GitHubRepoService;
import com.redcare.domain.GitHubRepo;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ControllerAdvice
@RestController
public class GitHubRepoController {

    private final GitHubRepoService repositoryService;

    public GitHubRepoController(GitHubRepoService repositoryService) {
        this.repositoryService = repositoryService;
    }

    @GetMapping("/repositories")
    public List<GitHubRepo> getRepositories(@RequestParam(name = "language") String language,
                                            @RequestParam(name = "createdAfter") String createdAfter,
                                            @RequestParam(name = "perPage", required = false) Integer perPage) {

        if (!isValidDateFormat(createdAfter)) {
            throw new InvalidDateFormatException("createdAfter parameter must be in YYYY-MM-DD format");
        }
        return repositoryService.getScoredRepositories(language, createdAfter, perPage);
    }

    private boolean isValidDateFormat(String dateStr) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate.parse(dateStr, formatter);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }
}
