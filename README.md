
## Endpoint

### Get Repositories with popular score

Fetch a list of repositories based on specific criteria.

- **URL:** `/repositories`
- **Method:** `GET`
- **URL Params:**
    - **language**: (required) The programming language of the repositories (e.g., `java`, `python`, `javascript`).
    - **createdAfter**: (required) The creation date filter in `YYYY-MM-DD` format. Only repositories created after this date will be included.

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
