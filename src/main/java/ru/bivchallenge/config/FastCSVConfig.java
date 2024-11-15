package ru.bivchallenge.config;

/**
 * Represents the configuration interface for FastCSV settings.
 * Provides methods to configure and retrieve FastCSV-specific parameters.
 */
public interface FastCSVConfig {

    /**
     * Retrieves the buffer size used by the FastCSV writer.
     *
     * @return the buffer size in bytes
     */
    int getWriterBufferSize();
}
