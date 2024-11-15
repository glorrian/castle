package ru.bivchallenge.persistence;

import de.siegmar.fastcsv.reader.CsvReader;
import de.siegmar.fastcsv.reader.CsvRecord;
import ru.bivchallenge.dto.Company;
import ru.bivchallenge.dto.Entity;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.function.BiFunction;
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
     * Loads data from the specified CSV table, parses each record into an entity,
     * and returns a map of entities identified by their unique IDs.
     * <p>
     * This method also supports repairing entities based on a repair function if parsing fails.
     *
     * @param tablePath the path to the CSV file
     * @param parseFunction a function to parse each {@link CsvRecord} into an entity
     * @param repairFunction a function to repair an entity in case parsing fails, based on the first field value
     * @return a map of entities, where the key is the entity ID, and the value is the entity itself
     * @throws RuntimeException if there is an error reading or processing the CSV file
     */
    protected Map<Long, T> getDataFromCsvTable(Path tablePath, Function<CsvRecord, T> parseFunction, BiFunction<String, T, T> repairFunction) {
        try (CsvReader<CsvRecord> csvReader = csvReaderBuilder.ofCsvRecord(tablePath)) {
            csvReader.skipLines(1);
            List<T> entities = new ArrayList<>();
            final int[] prev = {-1};
            csvReader.stream().forEach((csvRecord) -> {
                T entity = parseFunction.apply(csvRecord);
                if (entity == null) {
                    T apply = repairFunction.apply(csvRecord.getField(0), entities.get(prev[0]));
                    entities.set(prev[0], apply);
                } else {
                    entities.add(entity);
                    prev[0]++;
                }
            });
            return entities.stream().collect(Collectors.toMap(Entity::id, Function.identity()));} catch (IOException e) {
            throw new RuntimeException("Failed to load legal entities from CSV file", e);
        }
    }

    /**
     * Loads data from the specified CSV table and parses each record into an entity,
     * returning a map of entities identified by their unique IDs.
     *
     * @param tablePath the path to the CSV file
     * @param parseFunction a function to parse each {@link CsvRecord} into an entity
     * @return a map of entities, where the key is the entity ID, and the value is the entity itself
     * @throws RuntimeException if there is an error reading or processing the CSV file
     */
    protected Map<Long, T> getDataFromCsvTable(Path tablePath, Function<CsvRecord, T> parseFunction) {
        try (CsvReader<CsvRecord> csvReader = csvReaderBuilder.ofCsvRecord(tablePath)) {
            csvReader.skipLines(1);
            return csvReader.stream()
                    .map(parseFunction)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toMap(T::id, legalEntity -> legalEntity));
        } catch (IOException e) {
            throw new RuntimeException("Failed to load legal entities from CSV file", e);
        }
    }
}
