package com.redcare.domain;

import lombok.Data;


@Data
public class GitHubRepo {
    private String name;
    private int stars;
    private int forks;
    private String updatedAt;
    private int popularityScore;

}
