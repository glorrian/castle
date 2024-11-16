package ru.bivchallenge.module;

import dagger.Module;
import dagger.Provides;
import de.siegmar.fastcsv.reader.CsvReader;
import de.siegmar.fastcsv.writer.CsvWriter;
import de.siegmar.fastcsv.writer.LineDelimiter;
import ru.bivchallenge.config.CastleConfig;
import ru.bivchallenge.config.PropertiesConfig;

import javax.inject.Singleton;

@Module
public class LocalDataModule {
    private final String[] args;

    public LocalDataModule(String[] args) {
        this.args = args;
    }

    @Provides
    @Singleton
    CsvReader.CsvReaderBuilder provideCsvReaderBuilder() {
        return CsvReader.builder()
                .fieldSeparator('\t')
                .skipEmptyLines(true)
                .ignoreDifferentFieldCount(true)
                .detectBomHeader(true);
    }

    @Provides
    @Singleton
    CsvWriter.CsvWriterBuilder provideCsvWriterBuilder() {
        return CsvWriter.builder()
                .bufferSize(64 * 1024)
                .fieldSeparator('\t')
                .lineDelimiter(LineDelimiter.LF);
    }

    @Provides
    @Singleton
    CastleConfig provideCastleConfig() {
        return new PropertiesConfig(args);
    }
}
