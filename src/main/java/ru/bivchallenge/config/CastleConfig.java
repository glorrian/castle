package ru.bivchallenge.config;

/**
 * Represents the configuration interface for the castle application.
 * Provides methods to retrieve configuration details for FastCSV and table settings.
 */
public interface CastleConfig {

    /**
     * Retrieves the configuration settings for FastCSV.
     *
     * @return an instance of {@link FastCSVConfig} containing FastCSV configuration details
     */
    FastCSVConfig getFastCSVConfig();

    /**
     * Retrieves the configuration settings for tables.
     *
     * @return an instance of {@link TableConfig} containing table configuration details
     */
    TableConfig getTableConfig();
}
