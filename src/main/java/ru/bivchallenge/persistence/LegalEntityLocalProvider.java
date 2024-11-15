package ru.bivchallenge.persistence;

import de.siegmar.fastcsv.reader.CsvReader;
import de.siegmar.fastcsv.reader.CsvRecord;
import jakarta.inject.Inject;
import ru.bivchallenge.config.CastleConfig;
import ru.bivchallenge.dto.LegalEntity;

import java.nio.file.Path;
import java.util.Map;

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
        return new LegalEntity(
                Long.parseLong(csvRecord.getField(0)),
                Long.parseLong(csvRecord.getField(1)),
                csvRecord.getField(2),
                csvRecord.getField(3),
                csvRecord.getField(4)
        );
    }
}
