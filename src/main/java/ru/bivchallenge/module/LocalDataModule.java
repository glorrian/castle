package ru.bivchallenge.module;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import de.siegmar.fastcsv.reader.CsvReader;
import de.siegmar.fastcsv.writer.CsvWriter;
import de.siegmar.fastcsv.writer.LineDelimiter;
import ru.bivchallenge.config.CastleConfig;
import ru.bivchallenge.config.PropertiesConfig;
import ru.bivchallenge.dto.BenefeciarSet;
import ru.bivchallenge.dto.Company;
import ru.bivchallenge.dto.LegalEntity;
import ru.bivchallenge.dto.NaturalEntity;
import ru.bivchallenge.persistence.*;

import javax.inject.Singleton;

@Module
public abstract class LocalDataModule {

    @Provides
    @Singleton
    public CsvReader.CsvReaderBuilder provideCsvReaderBuilder() {
        return CsvReader.builder()
                .fieldSeparator('\t')
                .skipEmptyLines(true)
                .ignoreDifferentFieldCount(true)
                .acceptCharsAfterQuotes(false);
    }

    @Provides
    @Singleton
    public CsvWriter.CsvWriterBuilder provideCsvWriterBuilder() {
        return CsvWriter.builder()
                .bufferSize(64 * 1024)
                .fieldSeparator('\t')
                .lineDelimiter(LineDelimiter.LF);
    }

    @Binds
    @Singleton
    abstract CastleConfig bindCastleConfig(PropertiesConfig propertiesConfig);

    @Binds
    @Singleton
    abstract DataProvider<LegalEntity> bindLegalEntityLocalProvider(LegalEntityLocalProvider legalEntityLocalProvider);

    @Binds
    @Singleton
    abstract DataProvider<NaturalEntity> bindNaturalEntityLocalProvider(NaturalEntityLocalProvider naturalEntityLocalProvider);

    @Binds
    @Singleton
    abstract DataProvider<Company> bindCompanyLocalProvider(CompanyLocalProvider companyLocalProvider);

    @Binds
    @Singleton
    abstract DataDispatcher<BenefeciarSet> bindBenefeciarLocalDataDispatcher(BenefeciarLocalDataDispatcher benefeciarLocalDataDispatcher);
}
