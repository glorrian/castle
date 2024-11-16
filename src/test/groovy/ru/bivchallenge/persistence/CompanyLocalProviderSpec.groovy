package ru.bivchallenge.persistence

import de.siegmar.fastcsv.reader.CsvReader
import ru.bivchallenge.config.CastleConfig
import ru.bivchallenge.config.TableConfig
import spock.lang.Specification
import spock.lang.TempDir

import java.nio.file.Files
import java.nio.file.Path

class CompanyLocalProviderSpec extends Specification {

    @TempDir
    Path tempDir

    def "should parse and load valid companies from CSV"() {
        given:
        def csvFilePath = tempDir.resolve("legal_entities.csv")
        Files.write(csvFilePath, ("id\togrn\tinn\tfull_name\n" +
                "42671\t1021000861144\t1003405007\tМУНИЦИПАЛЬНОЕ ОБЩЕОБРАЗОВАТЕЛЬНОЕ УЧРЕЖДЕНИЕ \"МУНИЦИПАЛЬНЫЙ ОБЩЕОБРАЗОВАТЕЛЬНЫЙ ЛИЦЕЙ ИМЕНИ А.С. ПУШКИНА Г.КОНДОПОГИ\"\n" +
                "53821\t1021300661910\t1308078876\tОТДЕЛ МИНИСТЕРСТВА ВНУТРЕННИХ ДЕЛ РОССИЙСКОЙ ФЕДЕРАЦИИ ПО ЗУБОВО-ПОЛЯНСКОМУ МУНИЦИПАЛЬНОМУ РАЙОНУ").stripIndent().getBytes())

        and:
        def tableConfigMock = Mock(TableConfig) {
            getCompanyTablePath() >> csvFilePath
        }
        def castleConfigMock = Mock(CastleConfig) {
            getTableConfig() >> tableConfigMock
        }

        and:
        def builder = CsvReader.builder()
        builder.fieldSeparator('\t' as char)
        def provider = new CompanyLocalProvider(castleConfigMock, builder)

        when:
        def entityMap = provider.get()

        then:
        entityMap.size() == 2
        entityMap[42671L].id == 42671L
        entityMap[42671L].ogrn == "1021000861144"
        entityMap[42671L].inn == "1003405007"
        entityMap[53821L].id == 53821L
        entityMap[53821L].ogrn == "1021300661910"
        entityMap[53821L].inn == "1308078876"
    }

    def "should skip invalid rows in CSV"() {
        given:
        def csvFilePath = tempDir.resolve("invalid_companies.csv")
        Files.write(csvFilePath, ("id\togrn\tinn\tfull_name\n" +
                "42671\t1021000861144\t1003405007\tМУНИЦИПАЛЬНОЕ ОБЩЕОБРАЗОВАТЕЛЬНОЕ УЧРЕЖДЕНИЕ\n" +
                "\t1021300661910\t1308078876\tОТДЕЛ МИНИСТЕРСТВА ВНУТРЕННИХ ДЕЛ\n" +
                "53821\t\t1308078876\tINVALID ROW").stripIndent().getBytes())

        and:
        def tableConfigMock = Mock(TableConfig) {
            getCompanyTablePath() >> csvFilePath
        }
        def castleConfigMock = Mock(CastleConfig) {
            getTableConfig() >> tableConfigMock
        }

        and:
        def builder = CsvReader.builder()
        builder.fieldSeparator('\t' as char)
        def provider = new CompanyLocalProvider(castleConfigMock, builder)

        when:
        def entityMap = provider.get()

        then:
        entityMap.size() == 1
        entityMap[42671L].id == 42671L
        entityMap[42671L].ogrn == "1021000861144"
        entityMap[42671L].inn == "1003405007"
    }

    def "should throw exception when CSV file is missing"() {
        given:
        def csvFilePath = tempDir.resolve("nonexistent.csv")

        and:
        def tableConfigMock = Mock(TableConfig) {
            getCompanyTablePath() >> csvFilePath
        }
        def castleConfigMock = Mock(CastleConfig) {
            getTableConfig() >> tableConfigMock
        }

        and:
        def builder = CsvReader.builder()
        builder.fieldSeparator('\t' as char)
        def provider = new CompanyLocalProvider(castleConfigMock, builder)

        when:
        provider.get()

        then:
        def exception = thrown(RuntimeException)
        exception.message.contains("Failed to load legal entities from CSV file")
    }

    def "should cache companies after first load"() {
        given:
        def csvFilePath = tempDir.resolve("cached_companies.csv")
        Files.write(csvFilePath, ("id\togrn\tinn\tfull_name\n" +
                "42671\t1021000861144\t1003405007\tМУНИЦИПАЛЬНОЕ ОБЩЕОБРАЗОВАТЕЛЬНОЕ УЧРЕЖДЕНИЕ").stripIndent().getBytes())

        and:
        def tableConfigMock = Mock(TableConfig) {
            getCompanyTablePath() >> csvFilePath
        }
        def castleConfigMock = Mock(CastleConfig) {
            getTableConfig() >> tableConfigMock
        }

        and:
        def builder = CsvReader.builder()
        builder.fieldSeparator('\t' as char)
        def provider = new CompanyLocalProvider(castleConfigMock, builder)

        when:
        def firstLoad = provider.get()

        and:
        Files.write(csvFilePath, ("id\togrn\tinn\tfull_name\n" +
                "53821\t1021300661910\t1308078876\tОТДЕЛ МИНИСТЕРСТВА ВНУТРЕННИХ ДЕЛ").stripIndent().getBytes())

        def secondLoad = provider.get()

        then:
        firstLoad == secondLoad
        firstLoad.size() == 1
        firstLoad[42671L].id == 42671L
    }
}
