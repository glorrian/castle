package ru.bivchallenge.persistence;

import de.siegmar.fastcsv.reader.CsvReader;
import de.siegmar.fastcsv.reader.CsvRecord;
import jakarta.inject.Inject;
import ru.bivchallenge.config.CastleConfig;
import ru.bivchallenge.dto.Company;

import java.nio.file.Path;
import java.util.Map;

/**
 * The {@code CompanyLocalProvider} class is responsible for providing {@link Company} data
 * from a local CSV file. It extends {@link AbstractLocalDataProvider} to leverage common
 * CSV reading functionality.
 *
 * <p>This class uses a {@link CsvReader} to read and parse company data from the file specified
 * in the {@link CastleConfig}. It also provides mechanisms to repair data if necessary.
 *
 * @see AbstractLocalDataProvider
 * @see CsvReader
 * @see Company
 */
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
        companyMap = getDataFromCsvTable(companiesTablePath, this::parseCompany, this::repairCompany);
        return companyMap;
    }

    private Company parseCompany(CsvRecord csvRecord) {
        if (csvRecord.getFieldCount() < 4) {
           return null;
        }
        return new Company(
                Long.parseLong(csvRecord.getField(0)),
                csvRecord.getField(1),
                csvRecord.getField(2),
                csvRecord.getField(3)
        );
    }

    private Company repairCompany(String lastPart, Company company) {
        return new Company(
                company.id(),
                company.ogrn(),
                company.inn(),
                company.fullName() + lastPart
        );
    }
}
