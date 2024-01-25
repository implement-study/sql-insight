package tech.insight.core.engine

import org.junit.jupiter.api.Test
import tech.insight.core.command.CreateDatabase
import tech.insight.core.createDatabase


class DruidAnalyzerTest {

    @Test
    fun commandTest() {
        val analyzer = DruidAnalyzer
        assert(analyzer.analysisSql(createDatabase) is CreateDatabase)
    }
}


