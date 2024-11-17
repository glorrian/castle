package ru.bivchallenge.persistence;

import de.siegmar.fastcsv.writer.CsvWriter;
import jakarta.inject.Inject;
import ru.bivchallenge.config.CastleConfig;
import ru.bivchallenge.dto.Benefeciar;
import ru.bivchallenge.data.BenefeciarRegistry;

import java.nio.file.Path;
import java.util.Set;

/**
 * The {@code BenefeciarLocalDataDispatcher} class is responsible for writing {@link BenefeciarRegistry} data
 * to a local CSV file. It uses the FastCSV library to perform efficient CSV writing with configurable buffer sizes.
 *
 * <p>This class fetches the file path and CSV writer configuration from the provided {@link CastleConfig}.
 * The {@code dispatch} method writes company information followed by its associated beneficiaries into the CSV file.
 *
 * @see BenefeciarRegistry
 * @see CsvWriter
 * @see CastleConfig
 */
public class BenefeciarLocalDataDispatcher implements DataDispatcher<BenefeciarRegistry> {
    private final Path benefeciariesTablePath;
    private final CsvWriter.CsvWriterBuilder csvWriterBuilder;

    @Inject
    public BenefeciarLocalDataDispatcher(CastleConfig castleConfig, CsvWriter.CsvWriterBuilder csvWriterBuilder) {
        this.benefeciariesTablePath = castleConfig.getTableConfig().getBeneficiariesTablePath();
        this.csvWriterBuilder = csvWriterBuilder;

        if (castleConfig.getFastCSVConfig().getWriterBufferSize() != 0) {
            csvWriterBuilder.bufferSize(castleConfig.getFastCSVConfig().getWriterBufferSize());
        }
    }

    @Override
    public void dispatch(Set<BenefeciarRegistry> set) {
        try (CsvWriter csvWriter = csvWriterBuilder.build(benefeciariesTablePath)) {
            csvWriter.writeRecord("company_id", "ogrn", "inn", "full_name");
            csvWriter.writeRecord("", "inn", "full_name", "percent");
            for (BenefeciarRegistry benefeciarRegistry : set) {
                if (!benefeciarRegistry.getBeneficiaries().isEmpty()) {
                    csvWriter.writeRecord(benefeciarRegistry.getCompany().separate());
                    for (Benefeciar benefeciar: benefeciarRegistry.getBeneficiaries()) {
                        csvWriter.writeRecord(benefeciar.separate());
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to write data to " + benefeciariesTablePath, e);
        }
    }

}
