

# GitHub Repository Popularity Score

This is a Spring Boot application that provides an API to fetch GitHub repositories popularity score based on programming language and creation date.
score = (stars * 3) + (forks * 2) - daysSinceUpdate;

## Tool

- Java 11 or later
- Maven 3.6.0 or later

## Build 
mvn clean package
### Run
java -jar target/git-popularity-score.jar

## Endpoint

### Get Repositories with popular score

Fetch a list of repositories based on specific criteria.

- **URL:** `/repositories`
- **Method:** `GET`
- **URL Params:**
    - **language**: (required) The programming language of the repositories (e.g., `java`, `python`, `javascript`).
    - **createdAfter**: (required) The creation date filter in `YYYY-MM-DD` format. Only repositories created after this date will be included.
    - **perPage**: (optional) max number of records should be return

## Example Request

GET http://localhost:8080/repositories?language=java&createdAfter=2024-07-07


## Example Response

```json
[
    {
        "name": "datacenter-proxies-instructions",
        "stars": 29,
        "forks": 0,
        "updatedAt": "2024-07-10T17:08:50Z",
        "popularityScore": 86
    },
    {
        "name": "another-repo",
        "stars": 100,
        "forks": 20,
        "updatedAt": "2024-07-09T10:10:10Z",
        "popularityScore": 150
    }
]
