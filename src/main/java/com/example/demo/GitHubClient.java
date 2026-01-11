package com.example.demo;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
class GitHubClient {

    private final RestClient restClient;

    GitHubClient(RestClient.Builder restClientBuilder, @Value("${github.api.base-url}") String baseUrl) {
        this.restClient = restClientBuilder
                .baseUrl(baseUrl)
                .build();
    }

    List<Repository> getRepositories(String username) {
        return restClient.get()
                .uri("/users/{username}/repos", username)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                    if (response.getStatusCode().value() == 404) {
                        throw new UserNotFoundException();
                    }
                })
                .body(new ParameterizedTypeReference<>() {});
    }

    List<Branch> getBranches(String owner, String repo) {
        return restClient.get()
                .uri("/repos/{owner}/{repo}/branches", owner, repo)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
    }
}
