package ru.bivchallenge.persistence;

import de.siegmar.fastcsv.reader.CsvReader;
import de.siegmar.fastcsv.reader.CsvRecord;
import jakarta.inject.Inject;
import ru.bivchallenge.config.CastleConfig;
import ru.bivchallenge.dto.NaturalEntity;

import java.nio.file.Path;
import java.util.Map;

public class NaturalEntityLocalProvider extends AbstractLocalDataProvider<NaturalEntity> {
    private final Path legalEntityTablePath;

    private Map<Long, NaturalEntity> naturalEntityMap;

    @Inject
    public NaturalEntityLocalProvider(CastleConfig castleConfig, CsvReader.CsvReaderBuilder csvReaderBuilder) {
        super(csvReaderBuilder);
        this.legalEntityTablePath = castleConfig.getTableConfig().getFounderNaturalTablePath();
    }

    @Override
    public Map<Long, NaturalEntity> get() {
        if (naturalEntityMap != null) {
            return naturalEntityMap;
        }
        naturalEntityMap = getDataFromCsvTable(legalEntityTablePath, this::parseLegalEntity);
        return naturalEntityMap;
    }

    private NaturalEntity parseLegalEntity(CsvRecord csvRecord) {
        return new NaturalEntity(
                Long.parseLong(csvRecord.getField(0)),
                Long.parseLong(csvRecord.getField(1)),
                csvRecord.getField(2),
                csvRecord.getField(3),
                csvRecord.getField(4),
                csvRecord.getField(5)
        );
    }
}
