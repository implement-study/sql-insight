package tech.insight.core.engine.json

import java.io.File
import java.nio.file.Path
import tech.insight.core.bean.Column
import tech.insight.core.bean.Index
import tech.insight.core.bean.InsertRow
import tech.insight.core.bean.Table

/**
 * @author gongxuanzhangmelt@gmail.com
 */
class JsonPkIndex(private val table: Table) : Index {

    private lateinit var jsonFilePath: Path


    override fun rndInit() {
        jsonFilePath = JsonEngineSupport.getJsonFile(table).toPath()
    }

    override val id: Int
        get() = 1

    override fun table(): Table {
        return table
    }

    override val name: String
        get() = "json"

    override fun insert(row: InsertRow) {
        throw UnsupportedOperationException("json engine index dont support insert")
    }

    override val file: File
        get() = jsonFilePath.toFile()

    override fun columns(): List<Column> {
        throw UnsupportedOperationException("json don't support")
    }
}
