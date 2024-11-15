package ru.bivchallenge.persistence;

import dagger.Component;
import de.siegmar.fastcsv.writer.CsvWriter;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import ru.bivchallenge.config.CastleConfig;
import ru.bivchallenge.dto.Benefeciar;
import ru.bivchallenge.dto.BenefeciarSet;
import ru.bivchallenge.module.LocalDataModule;

import java.nio.file.Path;
import java.util.Set;

@Component(modules = LocalDataModule.class)
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
                csvWriter.writeRecord(benefeciarSet.company().separate());
                for (Benefeciar benefeciar: benefeciarSet.naturalEntitySet()) {
                    csvWriter.writeRecord(benefeciar.separate());
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to write data to " + benefeciariesTablePath, e);
        }
    }

}
