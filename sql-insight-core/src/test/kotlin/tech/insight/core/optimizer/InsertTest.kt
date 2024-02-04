package tech.insight.core.optimizer

import org.junit.jupiter.api.Test
import tech.insight.core.clearDatabase
import tech.insight.core.engine.SqlPipeline
import tech.insight.core.extension.tree
import tech.insight.core.insert
import java.io.FileInputStream


/**
 * @author gxz gongxuanzhangmelt@gmail.com
 **/
class InsertTest {

    @Test
    fun insertRow() {
        clearDatabase()
        ExecutePlanTest().createTableTest()
        SqlPipeline.doSql(insert)
    }

}
