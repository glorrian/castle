package ru.bivchallenge.persistence;

import de.siegmar.fastcsv.reader.CsvReader;
import de.siegmar.fastcsv.reader.CsvRecord;
import ru.bivchallenge.dto.Entity;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Abstract class providing the base implementation for loading data from CSV files
 * and converting it into a map of entities.
 *
 * @param <T> the type of entity that extends {@link Entity}
 */
public abstract class AbstractLocalDataProvider<T extends Entity> implements DataProvider<T> {

    private final CsvReader.CsvReaderBuilder csvReaderBuilder;

    /**
     * Constructor that initializes the CSV reader builder.
     *
     * @param csvReaderBuilder the builder for creating the CSV reader
     */
    protected AbstractLocalDataProvider(CsvReader.CsvReaderBuilder csvReaderBuilder) {
        this.csvReaderBuilder = csvReaderBuilder;
    }

    /**
     * Loads data from a CSV file and returns a map of entities.
     * The CSV file is parsed using the provided {@link Function} to convert each
     * {@link CsvRecord} into an entity of type {@link T}. The resulting map
     * uses the entity's ID as the key.
     *
     * @param tablePath the path to the CSV file containing the data
     * @param parseFunction a function to convert each {@link CsvRecord} into an entity of type {@link T}
     * @return a map of entities indexed by their ID
     * @throws RuntimeException if an error occurs while reading the CSV file
     */
    protected Map<Long, T> getDataFromCsvTable(Path tablePath, Function<CsvRecord, T> parseFunction) {
        try (CsvReader<CsvRecord> csvReader = csvReaderBuilder.ofCsvRecord(tablePath)) {
            return csvReader.stream()
                    .map(parseFunction)
                    .collect(Collectors.toMap(T::id, legalEntity -> legalEntity));
        } catch (IOException e) {
            throw new RuntimeException("Failed to load legal entities from CSV file", e);
        }
    }
}
