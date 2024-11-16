package ru.bivchallenge.persistence


import de.siegmar.fastcsv.writer.CsvWriter
import de.siegmar.fastcsv.writer.CsvWriter.CsvWriterBuilder
import ru.bivchallenge.config.CastleConfig
import ru.bivchallenge.config.FastCSVConfig
import ru.bivchallenge.config.TableConfig
import ru.bivchallenge.data.BenefeciarRegistry
import ru.bivchallenge.dto.Benefeciar
import ru.bivchallenge.dto.Company
import ru.bivchallenge.dto.NaturalEntity
import spock.lang.Specification
import spock.lang.TempDir

import java.nio.file.Files
import java.nio.file.Path

class BenefeciarLocalDataDispatcherSpec extends Specification {

    @TempDir
    Path tempDir

    def "should write BenefeciarRegistry data to a CSV file"() {
        given:
        def csvFilePath = tempDir.resolve("beneficiaries.tsv")
        def csvWriterBuilder = CsvWriter.builder()

        def castleConfigMock = Mock(CastleConfig)
        def tableConfigMock = Mock(TableConfig)
        def fastCSVConfig = Mock(FastCSVConfig)
        tableConfigMock.getBeneficiariesTablePath() >> csvFilePath
        fastCSVConfig.getWriterBufferSize() >> 1024
        castleConfigMock.getTableConfig() >> tableConfigMock
        castleConfigMock.getFastCSVConfig() >> fastCSVConfig

        and:
        def dispatcher = new BenefeciarLocalDataDispatcher(castleConfigMock, csvWriterBuilder)

        and:
        def company = new Company(1L, "1234567890123", "1234567890", "Test Company")
        def beneficiary = new Benefeciar(new NaturalEntity(1L, 1L, "123", "Doe", "John", ""), 0.5) // Replace `null` with the actual NaturalEntity if needed
        def registry = new BenefeciarRegistry(company)
        registry.getBeneficiaries().add(beneficiary)

        when:
        dispatcher.dispatch([registry] as Set)

        then:
        Files.exists(csvFilePath)

        and:
        def lines = Files.readAllLines(csvFilePath)
        lines.size() == 4 // with headers
        lines[2] == "1,1234567890123,1234567890,Test Company"
        lines[3] == beneficiary.separate().join(",")
    }

}
