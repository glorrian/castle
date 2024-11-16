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
    private final String[] args;

    @Inject
    public PropertiesConfig(String[] args) {
        properties = new Properties();
        this.args = args;
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
        boolean isProperty = args.length < 4;
        return createTableConfig(
                isProperty ? properties.getProperty("table.companies") : args[0],
                isProperty ? properties.getProperty("table.founder-legal") : args[1],
                isProperty ? properties.getProperty("table.founder-natural") : args[2],
                isProperty ? properties.getProperty("table.beneficiaries") : args[3]
        );
    }

    private TableConfig createTableConfig(String companyPath, String founderLegalPath, String founderNaturalPath, String beneficiariesPath) {
        return new TableConfig() {
            @Override
            public Path getCompanyTablePath() {
                return Path.of(companyPath);
            }

            @Override
            public Path getFounderLegalTablePath() {
                return Path.of(founderLegalPath);
            }

            @Override
            public Path getFounderNaturalTablePath() {
                return Path.of(founderNaturalPath);
            }

            @Override
            public Path getBeneficiariesTablePath() {
                return Path.of(beneficiariesPath);
            }
        };
    }
}

