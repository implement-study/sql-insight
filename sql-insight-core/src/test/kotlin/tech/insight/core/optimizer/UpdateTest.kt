package tech.insight.core.optimizer

import com.fasterxml.jackson.databind.JsonNode
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import tech.insight.core.*
import tech.insight.core.bean.Table
import tech.insight.core.engine.SqlPipeline
import tech.insight.core.engine.json.JsonEngineSupport
import tech.insight.core.environment.TableManager
import tech.insight.core.extension.tree


/**
 * @author gxz gongxuanzhangmelt@gmail.com
 **/
class UpdateTest {


    @BeforeEach
    @AfterEach
    fun clear() {
        clearDatabase()
    }

    @Test
    fun updateTest() {
        ExecutePlanTest().createTableTest()
        SqlPipeline.doSql(largeInsert)
        val table = TableManager.require(testDb, test_table)
        val old = associateJson(table)
        SqlPipeline.doSql(update)
        val new = associateJson(table)
        assertEquals(old.size, new.size)
        old.forEach { entry ->
            val newJsonNode = new[entry.key]!!
            assertEquals("${entry.value["name"].textValue()}new name", newJsonNode["name"].textValue())
        }
    }


    @Test
    fun updateWhereTest() {
        ExecutePlanTest().createTableTest()
        SqlPipeline.doSql(largeInsert)
        val table = TableManager.require(testDb, test_table)
        val old = associateJson(table)
        SqlPipeline.doSql(updateWhere)
        val new = associateJson(table)
        assertEquals(old.size, new.size)
        old.forEach { entry ->
            val newJsonNode = new[entry.key]!!
            if(entry.key >10){
                assertEquals("${entry.value["name"].textValue()}new name", newJsonNode["name"].textValue())
            }else{
                assertEquals(entry.value["name"].textValue(), newJsonNode["name"].textValue())
            }

        }
    }

    private fun associateJson(table: Table): Map<Int, JsonNode> {
        val jsonFile = JsonEngineSupport.getJsonFile(table)
        return jsonFile.readLines().asSequence()
            .filter { it.isNotEmpty() }
            .map { it.tree() }
            .associateBy { it["id"].intValue() }
    }


}
