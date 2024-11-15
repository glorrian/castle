package ru.bivchallenge.config;

import java.nio.file.Path;

/**
 * Represents the configuration interface for table file paths.
 * Provides methods to retrieve paths to different data tables.
 */
public interface TableConfig {

    /**
     * Retrieves the file path for the companies table.
     *
     * @return a {@link Path} to the companies table file
     */
    Path getCompaniesTablePath();

    /**
     * Retrieves the file path for the legal founders table.
     *
     * @return a {@link Path} to the legal founders table file
     */
    Path getFounderLegalTablePath();

    /**
     * Retrieves the file path for the natural founders table.
     *
     * @return a {@link Path} to the natural founders table file
     */
    Path getFounderNaturalTablePath();
}
