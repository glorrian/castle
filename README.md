# Castle

Welcome to the Castle project! This project is part of the Biv Hack Challenge and focuses on managing complex company ownership structures and calculating beneficiaries. The main motivation behind this project is to avoid reflection and support static compilation, ensuring efficient and secure code execution.

## Features

- **Company Graph Management**: Efficiently manage and visualize complex company ownership structures.
- **Beneficiary Calculation**: Accurately calculate the beneficiaries and their respective ownership percentages.
- **Gradle**: Utilizes Gradle for build automation and dependency management.
- **Dependency Injection**: Uses the Digger library for dependency injection to enhance modularity and testability.
- **GraalVM Support**: Enables efficient and optimized execution with GraalVM.

## Getting Started

### Prerequisites

- Docker
- Java 21
- Gradle
- GraalVM

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

This project uses Groovy with Spock for testing. To run the tests, use the following command:
```bash
./gradlew test
```

## Motivation and Principles
The Castle project aims to provide a robust solution for managing and calculating complex company ownership structures. Key principles include:
- **Avoiding Reflection**: Ensuring static compilation for better performance and security.
- **Static Compilation**: Leveraging static compilation to improve code efficiency and maintainability.

## License
This project is licensed under the GPL-3.0 License.