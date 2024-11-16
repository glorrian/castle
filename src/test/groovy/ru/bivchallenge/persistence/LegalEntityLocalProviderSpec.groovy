package ru.bivchallenge.persistence

import de.siegmar.fastcsv.reader.CsvReader
import ru.bivchallenge.config.CastleConfig
import ru.bivchallenge.config.TableConfig
import ru.bivchallenge.dto.LegalEntity
import spock.lang.Specification
import spock.lang.TempDir

import java.nio.file.Files
import java.nio.file.Path

class LegalEntityLocalProviderSpec extends Specification {

    @TempDir
    Path tempDir

    def "should parse and load valid legal entities from CSV"() {
        given:
        def csvFilePath = tempDir.resolve("legal_entities.csv")
        Files.write(csvFilePath, ("id\tcompany_id\togrn\tinn\tfull_name\tshare\tshare_percent\n" +
                "1\t6\t1037401354671\t7423019252\tОБЩЕСТВО С ОГРАНИЧЕННОЙ ОТВЕТСТВЕННОСТЬЮ \"АКРОН-ИНВЕСТ\"\t44454000.00\n" +
                "2\t8\t1021602841402\t1654017170\tМИНИСТЕРСТВО ЗДРАВООХРАНЕНИЯ РЕСПУБЛИКИ ТАТАРСТАН\t0.00\t").getBytes())

        and:
        def tableConfigMock = Mock(TableConfig) {
            getFounderLegalTablePath() >> csvFilePath
        }
        def castleConfigMock = Mock(CastleConfig) {
            getTableConfig() >> tableConfigMock
        }

        and:
        def builder = CsvReader.builder()
        builder.fieldSeparator('\t' as char)
        def provider = new LegalEntityLocalProvider(castleConfigMock, builder)

        when:
        Map<Long, LegalEntity> entityMap = provider.get()

        then:
        entityMap.size() == 2
        entityMap[1L].id == 1L
        entityMap[1L].getCompanyId() == 6L
        entityMap[1L].getOgrn() == "1037401354671"
        entityMap[1L].getInn() == "7423019252"
        entityMap[1L].getFullName() == "ОБЩЕСТВО С ОГРАНИЧЕННОЙ ОТВЕТСТВЕННОСТЬЮ \"АКРОН-ИНВЕСТ\""
        entityMap[1L].getShare() == 44454000.00
        entityMap[1L].getSharePercent() == 0.0

        entityMap[2L].id == 2L
        entityMap[2L].getCompanyId() == 8L
        entityMap[2L].getOgrn() == "1021602841402"
        entityMap[2L].getInn() == "1654017170"
        entityMap[2L].getFullName() == "МИНИСТЕРСТВО ЗДРАВООХРАНЕНИЯ РЕСПУБЛИКИ ТАТАРСТАН"
        entityMap[2L].getShare() == 0.00
        entityMap[2L].getSharePercent() == 0.0
    }

    def "should skip invalid rows in CSV"() {
        given:
        def csvFilePath = tempDir.resolve("invalid_legal_entities.csv")
        Files.write(csvFilePath, ("id\tcompany_id\togrn\tinn\tfull_name\tshare\tshare_percent\n" +
                "1\t6\t1037401354671\t7423019252\tОБЩЕСТВО С ОГРАНИЧЕННОЙ ОТВЕТСТВЕННОСТЬЮ \"АКРОН-ИНВЕСТ\"\t44454000.00\n" +
                "2\t8\t1021602841402\t1654017170\tМИНИСТЕРСТВО ЗДРАВООХРАНЕНИЯ РЕСПУБЛИКИ ТАТАРСТАН\n" +
                "3\t\t\t\tINVALID ROW\n" +
                "\t\t\t\tEMPTY ROW").getBytes())

        and:
        def tableConfigMock = Mock(TableConfig) {
            getFounderLegalTablePath() >> csvFilePath
        }
        def castleConfigMock = Mock(CastleConfig) {
            getTableConfig() >> tableConfigMock
        }

        and:
        def builder = CsvReader.builder()
        builder.fieldSeparator('\t' as char)
        def provider = new LegalEntityLocalProvider(castleConfigMock, builder)

        when:
        Map<Long, LegalEntity> entityMap = provider.get()

        then:
        entityMap.size() == 2
        entityMap[1L].id == 1L
        entityMap[1L].getCompanyId() == 6L
        entityMap[1L].getOgrn() == "1037401354671"
        entityMap[1L].getInn() == "7423019252"
        entityMap[1L].getFullName() == "ОБЩЕСТВО С ОГРАНИЧЕННОЙ ОТВЕТСТВЕННОСТЬЮ \"АКРОН-ИНВЕСТ\""
        entityMap[1L].getShare() == 44454000.00
        entityMap[1L].getSharePercent() == 0.0
        entityMap[2L].id == 2L
        entityMap[2L].getCompanyId() == 8L
        entityMap[2L].getOgrn() == "1021602841402"
        entityMap[2L].getInn() == "1654017170"
        entityMap[2L].getFullName() == "МИНИСТЕРСТВО ЗДРАВООХРАНЕНИЯ РЕСПУБЛИКИ ТАТАРСТАН"
        entityMap[2L].getShare() == 0.00
        entityMap[2L].getSharePercent() == 0.0
    }

    def "should throw exception when CSV file is missing"() {
        given:
        def csvFilePath = tempDir.resolve("missing.csv")

        and:
        def tableConfigMock = Mock(TableConfig) {
            getFounderLegalTablePath() >> csvFilePath
        }
        def castleConfigMock = Mock(CastleConfig) {
            getTableConfig() >> tableConfigMock
        }

        and:
        def builder = CsvReader.builder()
        builder.fieldSeparator('\t' as char)
        def provider = new LegalEntityLocalProvider(castleConfigMock, builder)

        when:
        provider.get()

        then:
        def exception = thrown(RuntimeException)
        exception.message.contains("Failed to load legal entities from CSV file")
    }

    def "should cache legal entities after first load"() {
        given:
        def csvFilePath = tempDir.resolve("cached_legal_entities.csv")
        Files.write(csvFilePath, ("id\tcompany_id\togrn\tinn\tfull_name\tshare\tshare_percent\n" +
                "1\t6\t1037401354671\t7423019252\tОБЩЕСТВО С ОГРАНИЧЕННОЙ ОТВЕТСТВЕННОСТЬЮ \"АКРОН-ИНВЕСТ\"\t44454000.00\n").getBytes())

        and:
        def tableConfigMock = Mock(TableConfig) {
            getFounderLegalTablePath() >> csvFilePath
        }
        def castleConfigMock = Mock(CastleConfig) {
            getTableConfig() >> tableConfigMock
        }

        and:
        def builder = CsvReader.builder()
        builder.fieldSeparator('\t' as char)
        def provider = new LegalEntityLocalProvider(castleConfigMock, builder)

        when:
        def firstLoad = provider.get()

        and:
        Files.write(csvFilePath, ("id\tcompany_id\togrn\tinn\tfull_name\tshare\tshare_percent\n" +
                "2\t8\t1021602841402\t1654017170\tМИНИСТЕРСТВО ЗДРАВООХРАНЕНИЯ РЕСПУБЛИКИ ТАТАРСТАН\t0.00\t").getBytes())

        def secondLoad = provider.get()

        then:
        firstLoad == secondLoad
        firstLoad.size() == 1
        firstLoad[1L].id == 1L
    }
}
