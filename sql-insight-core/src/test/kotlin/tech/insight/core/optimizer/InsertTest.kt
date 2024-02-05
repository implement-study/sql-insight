package tech.insight.core.optimizer

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import tech.insight.core.clearDatabase
import tech.insight.core.engine.SqlPipeline
import tech.insight.core.extension.tree
import tech.insight.core.insert
import tech.insight.core.largeInsert
import java.io.FileInputStream


/**
 * @author gxz gongxuanzhangmelt@gmail.com
 **/
class InsertTest {


    @BeforeEach
//    @AfterEach
    fun clear(){
        clearDatabase()
    }

    @Test
    fun insertRow() {
        ExecutePlanTest().createTableTest()
        SqlPipeline.doSql(insert)
    }


    @Test
    fun largeInsertTest(){
        ExecutePlanTest().createTableTest()
        SqlPipeline.doSql(largeInsert)

    }

}
