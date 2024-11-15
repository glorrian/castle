package ru.bivchallenge.persistence;

import dagger.Component;
import de.siegmar.fastcsv.reader.CsvReader;
import de.siegmar.fastcsv.reader.CsvRecord;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import ru.bivchallenge.config.CastleConfig;
import ru.bivchallenge.dto.Company;
import ru.bivchallenge.module.LocalDataModule;

import java.nio.file.Path;
import java.util.Map;

@Component(modules = LocalDataModule.class)
public class CompanyLocalProvider extends AbstractLocalDataProvider<Company> {
    private final Path companiesTablePath;

    private Map<Long, Company> companyMap;

    @Inject
    public CompanyLocalProvider(CastleConfig castleConfig, CsvReader.CsvReaderBuilder csvReaderBuilder) {
        super(csvReaderBuilder);
        this.companiesTablePath = castleConfig.getTableConfig().getCompanyTablePath();
    }

    @Override
    public Map<Long, Company> get() {
        if (companyMap != null) {
            return companyMap;
        }
        companyMap = getDataFromCsvTable(companiesTablePath, this::parseCompany);
        return companyMap;
    }

    private Company parseCompany(CsvRecord record) {
        return new Company(
                Long.parseLong(record.getField(0)),
                record.getField(1),
                record.getField(2),
                record.getField(3)
        );
    }
}
