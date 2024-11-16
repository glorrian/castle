package ru.bivchallenge.persistence;

import de.siegmar.fastcsv.reader.CsvReader;
import de.siegmar.fastcsv.reader.CsvRecord;
import jakarta.inject.Inject;
import ru.bivchallenge.config.CastleConfig;
import ru.bivchallenge.dto.LegalEntity;

import java.nio.file.Path;
import java.util.Map;

/**
 * The {@code LegalEntityLocalProvider} class is responsible for loading {@link LegalEntity} data
 * from a local CSV file. It extends {@link AbstractLocalDataProvider} to utilize common CSV reading functionality.
 * <p>
 * This class reads legal entity information from a file specified in the {@link CastleConfig} and
 * parses it into a map where the keys are legal entity IDs and the values are {@link LegalEntity} objects.
 *
 * @see AbstractLocalDataProvider
 * @see CsvReader
 * @see LegalEntity
 */
public class LegalEntityLocalProvider extends AbstractLocalDataProvider<LegalEntity> {
    private final Path legalEntityTablePath;

    private Map<Long, LegalEntity> legalEntityMap;

    @Inject
    public LegalEntityLocalProvider(CastleConfig castleConfig, CsvReader.CsvReaderBuilder csvReaderBuilder) {
        super(csvReaderBuilder);
        this.legalEntityTablePath = castleConfig.getTableConfig().getFounderLegalTablePath();
    }


    @Override
    public Map<Long, LegalEntity> get() {
        if (legalEntityMap != null) {
            return legalEntityMap;
        }
        legalEntityMap = getDataFromCsvTable(legalEntityTablePath, this::parseLegalEntity);
        return legalEntityMap;
    }

    private LegalEntity parseLegalEntity(CsvRecord csvRecord) {
        if (csvRecord.getFieldCount() < 5) {
            return null;
        }
        try {
            LegalEntity legalEntity = new LegalEntity(
                    Long.parseLong(csvRecord.getField(0)),
                    Long.parseLong(csvRecord.getField(1)),
                    csvRecord.getField(2),
                    csvRecord.getField(3),
                    csvRecord.getField(4)
            );
            if (csvRecord.getFieldCount() >= 6) {
                legalEntity.setShare(Double.parseDouble(csvRecord.getField(5).isEmpty() ? "0" : csvRecord.getField(5)));
            }
            if (csvRecord.getFieldCount() == 7) {
                legalEntity.setSharePercent(Double.parseDouble(csvRecord.getField(6).isEmpty() ? "0" : csvRecord.getField(6)));
            }
            return legalEntity;
        } catch (Exception e) {
            return null;
        }
    }
}
