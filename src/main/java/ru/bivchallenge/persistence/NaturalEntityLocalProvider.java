package ru.bivchallenge.persistence;

import de.siegmar.fastcsv.reader.CsvReader;
import de.siegmar.fastcsv.reader.CsvRecord;
import jakarta.inject.Inject;
import ru.bivchallenge.config.CastleConfig;
import ru.bivchallenge.dto.NaturalEntity;

import java.nio.file.Path;
import java.util.Map;

/**
 * The {@code NaturalEntityLocalProvider} class is responsible for loading {@link NaturalEntity} data
 * from a local CSV file. It extends {@link AbstractLocalDataProvider} to reuse common CSV reading functionality.
 * <p>
 * This class reads natural entity information from a file specified in the {@link CastleConfig}
 * and parses it into a map where the keys are natural entity IDs and the values are {@link NaturalEntity} objects.
 *
 * @see AbstractLocalDataProvider
 * @see CsvReader
 * @see NaturalEntity
 */
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
        if (csvRecord.getFieldCount() < 6) {
            return null;
        }
        NaturalEntity naturalEntity = new NaturalEntity(
                Long.parseLong(csvRecord.getField(0)),
                Long.parseLong(csvRecord.getField(1)),
                csvRecord.getField(2),
                csvRecord.getField(3),
                csvRecord.getField(4),
                csvRecord.getField(5)
        );
        if (csvRecord.getFieldCount() == 8) {
            naturalEntity.setShare(Double.parseDouble(csvRecord.getField(6).isEmpty() ? "0" : csvRecord.getField(6)));
            naturalEntity.setSharePercent(Double.parseDouble(csvRecord.getField(7).isEmpty() ? "0" : csvRecord.getField(7)));
        }
        return naturalEntity;
    }
}
