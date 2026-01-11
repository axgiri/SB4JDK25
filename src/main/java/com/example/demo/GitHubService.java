package com.example.demo;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
class GitHubService {

    private final GitHubClient gitHubClient;

    GitHubService(GitHubClient gitHubClient) {
        this.gitHubClient = gitHubClient;
    }

    List<Repository> getUserRepositories(String username) {
        return gitHubClient.getRepositories(username).stream()
                .filter(repo -> !repo.fork())
                .map(repo -> repo.withBranches(gitHubClient.getBranches(repo.owner().login(), repo.name())))
                .toList();
    }
}