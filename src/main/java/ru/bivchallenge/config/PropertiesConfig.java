package ru.bivchallenge.config;

import jakarta.inject.Inject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Properties;

/**
 * The {@code PropertiesConfig} class is an implementation of the {@link CastleConfig} interface
 * that loads configuration properties from a file named {@code application.properties}.
 * <p>
 * This class uses the {@link Properties} object to manage configuration values and provides
 * methods to retrieve configurations for {@link FastCSVConfig} and {@link TableConfig}.
 *
 * <p>The configuration file must be located in the classpath, and its properties are loaded during
 * the initialization of this class. Any failure in loading the file results in a {@link RuntimeException}.
 *
 * @see CastleConfig
 * @see FastCSVConfig
 * @see TableConfig
 */
public class PropertiesConfig implements CastleConfig {

    private final Properties properties;

    @Inject
    public PropertiesConfig() {
        properties = new Properties();
        InputStream input = PropertiesConfig.class.getClassLoader().getResourceAsStream("application.properties");
        try {
            properties.load(input);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load properties from application.properties", e);
        }

    }

    @Override
    public FastCSVConfig getFastCSVConfig() {
        return () -> Integer.parseInt(properties.getProperty("fastcsv.writer.buffer.size"));
    }

    @Override
    public TableConfig getTableConfig() {
        return new TableConfig() {
            @Override
            public Path getCompanyTablePath() {
                return Path.of(properties.getProperty("table.companies"));
            }

            @Override
            public Path getFounderLegalTablePath() {
                return Path.of(properties.getProperty("table.founder-legal"));
            }

            @Override
            public Path getFounderNaturalTablePath() {
                return Path.of(properties.getProperty("table.founder-natural"));
            }

            @Override
            public Path getBeneficiariesTablePath() {
                return Path.of(properties.getProperty("table.beneficiaries"));
            }
        };
    }
}
