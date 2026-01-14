package com.example.demo;

import org.jspecify.annotations.NullMarked;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@NullMarked
@RestController
final class GitHubController {

    private final GitHubService gitHubService;

    GitHubController(final GitHubService gitHubService) {
        this.gitHubService = gitHubService;
    }

    @GetMapping(value = "/api/v1/repositories/{username}", produces = "application/json")
    List<Repository> getUserRepositories(@PathVariable final String username) {
        return gitHubService.getUserRepositories(username);
    }

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    ErrorResponse handleUserNotFound(final UserNotFoundException ex) {
        return new ErrorResponse(404, ex.getMessage());
    }
}
