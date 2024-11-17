# castle
Project for the second case for the Biv Hack Challenge

Build from source with Docker:

1. Put tables into .local folder in the root of the project
2. Run
```bash
docker build -t castle .
docker run --name temp_container  castle-application
docker cp temp_container:app/.local/beneficiaries.tsv path/to/data
docker rm temp_container
- ```
