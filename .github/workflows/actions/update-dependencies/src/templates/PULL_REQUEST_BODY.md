## Short description

Updated microservice dependencies

## List of changes proposed in this pull request

{{CHANGES}}

## How to test

1. Run:
    ```bash
    mvn clean install
    ```
    This installs dependencies and runs both unit and integration tests.

2. From the root of the project, run:
    ```bash
    ./scripts/generate-code.sh
    ```
    This regenerates the external and AWS OpenAPI files.

    > **Note:** Make sure Rancher Desktop (or Docker) is running before executing this command.