package ru.bivchallenge.persistence

import de.siegmar.fastcsv.reader.CsvReader
import ru.bivchallenge.config.CastleConfig
import ru.bivchallenge.config.TableConfig
import ru.bivchallenge.dto.NaturalEntity
import spock.lang.Specification
import spock.lang.TempDir

import java.nio.file.Files
import java.nio.file.Path

class NaturalEntityLocalProviderSpec extends Specification {

    @TempDir
    Path tempDir

    def "should parse and load valid natural entities from CSV"() {
        given:
        def csvFilePath = tempDir.resolve("natural_entities.csv")
        Files.write(csvFilePath, ("id\tcompany_id\tinn\tlast_name\tfirst_name\tsecond_name\tshare\tshare_percent\n" +
                "18\t13\t364502537688\tСАВВИНОВА\tАНАСТАСИЯ\tВЛАДИМИРОВНА\t14000.00\t1.00\n" +
                "19\t15\t364500288113\tШИРЯЕВА\tСВЕТЛАНА\tЛЕОНИДОВНА\t4000.00\t0.40\n").getBytes())

        and:
        def tableConfigMock = Mock(TableConfig) {
            getFounderNaturalTablePath() >> csvFilePath
        }
        def castleConfigMock = Mock(CastleConfig) {
            getTableConfig() >> tableConfigMock
        }

        and:
        def builder = CsvReader.builder()
        builder.fieldSeparator('\t' as char)
        def provider = new NaturalEntityLocalProvider(castleConfigMock, builder)

        when:
        Map<Long, NaturalEntity> entityMap = provider.get()

        then:
        entityMap.size() == 2
        entityMap[18L].id == 18L
        entityMap[18L].getCompanyId() == 13L
        entityMap[18L].getInn() == "364502537688"
        entityMap[18L].getLastName() == "САВВИНОВА"
        entityMap[18L].getFirstName() == "АНАСТАСИЯ"
        entityMap[18L].getSecondName() == "ВЛАДИМИРОВНА"
        entityMap[18L].getShare() == 14000.00
        entityMap[18L].getSharePercent() == 1.00

        entityMap[19L].id == 19L
        entityMap[19L].getCompanyId() == 15L
        entityMap[19L].getInn() == "364500288113"
        entityMap[19L].getLastName() == "ШИРЯЕВА"
        entityMap[19L].getFirstName() == "СВЕТЛАНА"
        entityMap[19L].getSecondName() == "ЛЕОНИДОВНА"
        entityMap[19L].getShare() == 4000.00
        entityMap[19L].getSharePercent() == 0.40
    }

    def "should skip invalid rows in CSV"() {
        given:
        def csvFilePath = tempDir.resolve("invalid_natural_entities.csv")
        Files.write(csvFilePath, ("id\tcompany_id\tinn\tlast_name\tfirst_name\tsecond_name\tshare\tshare_percent\n" +
                "18\t13\t364502537688\tСАВВИНОВА\tАНАСТАСИЯ\tВЛАДИМИРОВНА\t14000.00\t1.00\n" +
                "19\t15\t364500288113\tШИРЯЕВА\tСВЕТЛАНА\tЛЕОНИДОВНА\n" +
                "20\t\t\t\tINVALID\tROW\n").getBytes())

        and:
        def tableConfigMock = Mock(TableConfig) {
            getFounderNaturalTablePath() >> csvFilePath
        }
        def castleConfigMock = Mock(CastleConfig) {
            getTableConfig() >> tableConfigMock
        }

        and:
        def builder = CsvReader.builder()
        builder.fieldSeparator('\t' as char)
        def provider = new NaturalEntityLocalProvider(castleConfigMock, builder)

        when:
        Map<Long, NaturalEntity> entityMap = provider.get()

        then:
        entityMap.size() == 2
        entityMap[18L].id == 18L
        entityMap[18L].getCompanyId() == 13L
        entityMap[18L].getInn() == "364502537688"
        entityMap[18L].getLastName() == "САВВИНОВА"
        entityMap[18L].getFirstName() == "АНАСТАСИЯ"
        entityMap[18L].getSecondName() == "ВЛАДИМИРОВНА"
        entityMap[18L].getShare() == 14000.00
        entityMap[18L].getSharePercent() == 1.00
    }

    def "should throw exception when CSV file is missing"() {
        given:
        def csvFilePath = tempDir.resolve("missing.csv")

        and:
        def tableConfigMock = Mock(TableConfig) {
            getFounderNaturalTablePath() >> csvFilePath
        }
        def castleConfigMock = Mock(CastleConfig) {
            getTableConfig() >> tableConfigMock
        }

        and:
        def builder = CsvReader.builder()
        builder.fieldSeparator('\t' as char)
        def provider = new NaturalEntityLocalProvider(castleConfigMock, builder)

        when:
        provider.get()

        then:
        thrown(RuntimeException)
    }

    def "should cache natural entities after first load"() {
        given:
        def csvFilePath = tempDir.resolve("cached_natural_entities.csv")
        Files.write(csvFilePath, ("id\tcompany_id\tinn\tlast_name\tfirst_name\tsecond_name\tshare\tshare_percent\n" +
                "18\t13\t364502537688\tСАВВИНОВА\tАНАСТАСИЯ\tВЛАДИМИРОВНА\t14000.00\t1.00\n").getBytes())

        and:
        def tableConfigMock = Mock(TableConfig) {
            getFounderNaturalTablePath() >> csvFilePath
        }
        def castleConfigMock = Mock(CastleConfig) {
            getTableConfig() >> tableConfigMock
        }

        and:
        def builder = CsvReader.builder()
        builder.fieldSeparator('\t' as char)
        def provider = new NaturalEntityLocalProvider(castleConfigMock, builder)

        when:
        def firstLoad = provider.get()

        and:
        Files.write(csvFilePath, ("id\tcompany_id\tinn\tlast_name\tfirst_name\tsecond_name\tshare\tshare_percent\n" +
                "19\t15\t364500288113\tШИРЯЕВА\tСВЕТЛАНА\tЛЕОНИДОВНА\t4000.00\t0.40\n").getBytes())

        def secondLoad = provider.get()

        then:
        firstLoad == secondLoad
        firstLoad.size() == 1
        firstLoad[18L].id == 18L
    }
}
