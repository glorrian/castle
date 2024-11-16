package ru.bivchallenge.persistence;

import de.siegmar.fastcsv.writer.CsvWriter;
import jakarta.inject.Inject;
import ru.bivchallenge.config.CastleConfig;
import ru.bivchallenge.dto.Benefeciar;
import ru.bivchallenge.dto.BenefeciarSet;

import java.nio.file.Path;
import java.util.Set;

public class BenefeciarLocalDataDispatcher implements DataDispatcher<BenefeciarSet> {
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
    public void dispatch(Set<BenefeciarSet> set) {
        try (CsvWriter csvWriter = csvWriterBuilder.build(benefeciariesTablePath)) {
            for (BenefeciarSet benefeciarSet : set) {
                csvWriter.writeRecord(benefeciarSet.getCompany().separate());
                for (Benefeciar benefeciar: benefeciarSet.getBeneficiaries()) {
                    csvWriter.writeRecord(benefeciar.separate());
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to write data to " + benefeciariesTablePath, e);
        }
    }

}
