package ru.bivchallenge.config;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Properties;

public class PropertiesConfig implements CastleConfig {

    private final Properties properties;

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
            public Path getCompaniesTablePath() {
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
        };
    }
}
