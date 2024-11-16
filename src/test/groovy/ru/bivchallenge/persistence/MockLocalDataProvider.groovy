package ru.bivchallenge.persistence

import de.siegmar.fastcsv.reader.CsvReader
import de.siegmar.fastcsv.reader.CsvRecord

import java.nio.file.Path
import java.util.function.BiFunction
import java.util.function.Function

class MockLocalDataProvider extends AbstractLocalDataProvider<MockEntity> {

    private Path csvFilePath

    MockLocalDataProvider(CsvReader.CsvReaderBuilder csvReaderBuilder, Path csvFilePath) {
        super(csvReaderBuilder)
        this.csvFilePath = csvFilePath
    }

    @Override
    Map<Long, MockEntity> get() {
        return getDataFromCsvTable(csvFilePath, parseFunction, repairFunction)
    }

    Function<CsvRecord, MockEntity> parseFunction = { record ->
        println record
        try {
            if (record.getFieldCount() < 3 || record.getField(1).isEmpty()) return null
            return new MockEntity(
                    Long.parseLong(record.getField(0)),
                    record.getField(1),
                    record.getField(2)
            )
        } catch (Exception ignored) {
            return null
        }
    }

    BiFunction<String, MockEntity, MockEntity> repairFunction = { id, entity ->
        new MockEntity(
                Long.parseLong(id),
                "Repaired Name",
                entity.description
        )
    }
}
