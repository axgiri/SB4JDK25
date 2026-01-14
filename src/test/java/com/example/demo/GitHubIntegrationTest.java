package com.example.demo;

import com.github.tomakehurst.wiremock.client.WireMock;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.wiremock.spring.ConfigureWireMock;
import org.wiremock.spring.EnableWireMock;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestRestTemplate
@EnableWireMock(@ConfigureWireMock(name = "github-api", baseUrlProperties = "github.api.base-url"))
class GitHubIntegrationTest {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Test
    void returnsRepositoriesWithBranchesFilteringOutForks() throws JSONException {
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
                                        "commit": {"sha": "abc123"}
                                    },
                                    {
                                        "name": "develop",
                                        "commit": {"sha": "def456"}
                                    }
                                ]
                                """)));

        final var expectedJson = """
                [
                    {
                        "name": "repo1",
                        "owner": {"login": "testuser"},
                        "fork": false,
                        "branches": [
                            {"name": "main", "commit": {"sha": "abc123"}},
                            {"name": "develop", "commit": {"sha": "def456"}}
                        ]
                    }
                ]
                """;

        final var response = testRestTemplate.getForEntity("/api/v1/repositories/testuser", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        JSONAssert.assertEquals(expectedJson, response.getBody(), JSONCompareMode.STRICT);
    }

    @Test
    void returns404ForNonExistingUser() throws JSONException {
        stubFor(WireMock.get(urlEqualTo("/users/nonexistentuser/repos"))
                .willReturn(aResponse()
                        .withStatus(404)
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                                {"message": "Not Found"}
                                """)));

        final var expectedJson = """
                {
                    "status": 404,
                    "message": "User not found"
                }
                """;

        final var response = testRestTemplate.getForEntity("/api/v1/repositories/nonexistentuser", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        JSONAssert.assertEquals(expectedJson, response.getBody(), JSONCompareMode.STRICT);
    }

    @Test
    void returnsEmptyListForUserWithNoRepositories() throws JSONException {
        stubFor(WireMock.get(urlEqualTo("/users/emptyuser/repos"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("[]")));

        final var expectedJson = "[]";

        final var response = testRestTemplate.getForEntity("/api/v1/repositories/emptyuser", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        JSONAssert.assertEquals(expectedJson, response.getBody(), JSONCompareMode.STRICT);
    }
}
