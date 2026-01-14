package com.example.demo;

import org.jspecify.annotations.NullMarked;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@NullMarked
@Configuration
class GitHubClientConfig {

    @Bean
    GitHubClient gitHubClient(
            final RestClient.Builder restClientBuilder,
            @Value("${github.api.base-url}") final String baseUrl
    ) {
        final var restClient = restClientBuilder
                .baseUrl(baseUrl)
                .defaultStatusHandler(HttpStatusCode::is4xxClientError, (request, response) -> {
                    if (response.getStatusCode().value() == 404) {
                        throw new UserNotFoundException();
                    }
                })
                .build();

        final var adapter = RestClientAdapter.create(restClient);
        final var factory = HttpServiceProxyFactory.builderFor(adapter).build();

        return factory.createClient(GitHubClient.class);
    }
}
