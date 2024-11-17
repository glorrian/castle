# Castle

Welcome to the Castle project! This project is part of the Biv Hack Challenge and focuses on managing complex company ownership structures and calculating beneficiaries.

## Features

- **Company Graph Management**: Efficiently manage and visualize complex company ownership structures.
- **Beneficiary Calculation**: Accurately calculate the beneficiaries and their respective ownership percentages.
- **Gradle**: Utilizes Gradle for build automation and dependency management.

## Getting Started

### Prerequisites

- Docker
- Java 21
- Gradle

### Build and Run with Docker

1. Place your tables into the `.local` folder in the root of the project.
2. Build the Docker image:
    ```bash
    docker build -t castle .
    ```
3. Run the Docker container:
    ```bash
    docker run --name temp_container castle-application
    ```
4. Copy the generated beneficiaries file from the container:
    ```bash
    docker cp temp_container:/app/.local/beneficiaries.tsv path/to/data
    ```
5. Remove the temporary container:
    ```bash
    docker rm temp_container
    ```

### Running Tests

To run the tests, use the following command:
```bash
./gradlew test