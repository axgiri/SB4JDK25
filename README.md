# GitHub Repository Proxy API

A Spring Boot application that proxies the GitHub API to list all non-fork repositories of a user, including each branch name and its last commit sha.

## Requirements

- Java 25
- Gradle

## Tech Stack

- Java 25
- Spring Boot 4.0.1
- Spring Web MVC
- Spring RestClient

## Running the Application

```bash
./gradlew bootRun
```

The application starts on `http://localhost:8080`

## API Endpoint

### List User Repositories

```
GET /api/repositories/{username}
Accept: application/json
```

**Success Response (200 OK):**
```json
[
    {
        "name": "repository-name",
        "owner": {
            "login": "name"
        },
        "branches": [
            {
                "name": "master",
                "commit": {
                    "sha": "abc123def456"
                }
            }
        ]
    }
]
```

**Error Response (404 Not Found):**
```json
{
    "status": 404,
    "message": "User not found: username"
}
```

## Running Tests

```bash
./gradlew test
```
