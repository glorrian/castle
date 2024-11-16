package ru.bivchallenge.persistence

import de.siegmar.fastcsv.reader.CsvReader
import spock.lang.Specification
import spock.lang.TempDir

import java.nio.file.Files
import java.nio.file.Path

class AbstractLocalDataProviderSpec extends Specification {

    @TempDir
    Path tempDir

    def "should parse and load valid entities from CSV"() {
        given:
        def csvFilePath = tempDir.resolve("entities.csv")
        Files.write(csvFilePath,
                ("ID,Name,Description\n" + "1,Entity1,Description1\n" + "2,Entity2,Description2").stripIndent().getBytes())

        and:
        def provider = new MockLocalDataProvider(CsvReader.builder(), csvFilePath)

        when:
        def entityMap = provider.getDataFromCsvTable(csvFilePath, provider.parseFunction)

        then:
        entityMap.size() == 2
        entityMap[1L].id() == 1L
        entityMap[1L].name == "Entity1"
        entityMap[1L].description == "Description1"
        entityMap[2L].id() == 2L
        entityMap[2L].name == "Entity2"
        entityMap[2L].description == "Description2"
    }

    def "should skip invalid rows in CSV"() {
        given:
        def csvFilePath = tempDir.resolve("invalid_entities.csv")
        Files.write(csvFilePath,
                ("ID,Name,Description\n" + "1,Entity1,Description1\n" + ",Entity2," + "\n3,,Description3").stripIndent().getBytes())

        and:
        def provider = new MockLocalDataProvider(CsvReader.builder(), csvFilePath)

        when:
        def entityMap = provider.getDataFromCsvTable(csvFilePath, provider.parseFunction)

        then:
        entityMap.size() == 1
        entityMap[1L].id() == 1L
        entityMap[1L].name == "Entity1"
        entityMap[1L].description == "Description1"
    }

    def "should throw exception if the CSV file is missing"() {
        given:
        def csvFilePath = tempDir.resolve("nonexistent.csv")

        and:
        def provider = new MockLocalDataProvider(CsvReader.builder(), csvFilePath)

        when:
        provider.getDataFromCsvTable(csvFilePath, provider.parseFunction)

        then:
        def exception = thrown(RuntimeException)
        exception.message.contains("Failed to load legal entities from CSV file")
    }
}
