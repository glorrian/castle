# castle
Project for the second case for the Biv Hack Challenge

Build from source with Docker:

- Put tables into .local folder in the root of the project
- Run
```bash
docker build -t castle .
docker run --name temp_container  castle-application
docker cp temp_container:app/.local/beneficiaries.tsv /Users/stanislavshcherbakov/Downloads
docker rm temp_container
- ```
