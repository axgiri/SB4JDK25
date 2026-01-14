package com.example.demo;

import org.jspecify.annotations.NullMarked;
import org.springframework.stereotype.Service;

import java.util.List;

@NullMarked
@Service
final class GitHubService {

    private final GitHubClient gitHubClient;

    GitHubService(final GitHubClient gitHubClient) {
        this.gitHubClient = gitHubClient;
    }

    List<Repository> getUserRepositories(final String username) {
        return gitHubClient.getRepositories(username).stream()
                .filter(repo -> !repo.fork())
                .map(repo -> repo.withBranches(gitHubClient.getBranches(repo.owner().login(), repo.name())))
                .toList();
    }
}