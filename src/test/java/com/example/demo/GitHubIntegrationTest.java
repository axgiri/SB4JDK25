package com.example.demo;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.web.client.RestClient;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GitHubIntegrationTest {

    private static WireMockServer wireMockServer;

    @LocalServerPort
    private int port;

    @Autowired
    private RestClient.Builder restClientBuilder;

    @BeforeAll
    static void startWireMock() {
        wireMockServer = new WireMockServer(0);
        wireMockServer.start();
        WireMock.configureFor(wireMockServer.port());
    }

    @AfterAll
    static void stopWireMock() {
        wireMockServer.stop();
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("github.api.base-url", () -> "http://localhost:" + wireMockServer.port());
    }

    private RestClient testClient() {
        return restClientBuilder.baseUrl("http://localhost:" + port).build();
    }

    @Test
    void returnsRepositoriesWithBranches() {
        stubFor(WireMock.get(urlEqualTo("/users/testuser/repos"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                                [
                                    {
                                        "name": "repo1",
                                        "owner": {"login": "testuser"},
                                        "fork": false
                                    },
                                    {
                                        "name": "forked-repo",
                                        "owner": {"login": "testuser"},
                                        "fork": true
                                    }
                                ]
                                """)));

        stubFor(WireMock.get(urlEqualTo("/repos/testuser/repo1/branches"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                                [
                                    {
                                        "name": "main",
                                        "commit": {"sha": "somesha"}
                                    },
                                    {
                                        "name": "develop",
                                        "commit": {"sha": "somesha2"}
                                    }
                                ]
                                """)));

        String response = testClient().get()
                .uri("/api/repositories/testuser")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(String.class);

        assertThat(response)
                .contains("\"name\":\"repo1\"")
                .contains("\"login\":\"testuser\"")
                .contains("\"name\":\"main\"")
                .contains("\"sha\":\"somesha\"")
                .contains("\"name\":\"develop\"")
                .contains("\"sha\":\"somesha2\"")
                .doesNotContain("forked-repo");
    }

    @Test
    void returns404ForNonExistingUser() {
        stubFor(WireMock.get(urlEqualTo("/users/nonexistentuser/repos"))
                .willReturn(aResponse()
                        .withStatus(404)
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                                {"message": "Not Found"}
                                """)));

        String response = testClient().get()
                .uri("/api/repositories/nonexistentuser")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(status -> status.value() == 404, (req, res) -> {})
                .body(String.class);

        assertThat(response)
                .contains("\"status\":404")
                .contains("\"message\":\"User not found\"");
    }
}
