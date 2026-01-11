package com.example.demo;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
class GitHubController {

    private final GitHubService gitHubService;

    GitHubController(GitHubService gitHubService) {
        this.gitHubService = gitHubService;
    }

    @GetMapping(value = "/api/repositories/{username}", produces = "application/json")
    List<Repository> getUserRepositories(@PathVariable String username) {
        return gitHubService.getUserRepositories(username);
    }

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    ErrorResponse handleUserNotFound(UserNotFoundException ex) {
        return new ErrorResponse(404, ex.getMessage());
    }
}
