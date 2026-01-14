package com.example.demo;

import org.jspecify.annotations.NullMarked;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

import java.util.List;

@NullMarked
@HttpExchange
interface GitHubClient {

    @GetExchange("/users/{username}/repos")
    List<Repository> getRepositories(@PathVariable String username);

    @GetExchange("/repos/{owner}/{repo}/branches")
    List<Branch> getBranches(@PathVariable String owner, @PathVariable String repo);
}
