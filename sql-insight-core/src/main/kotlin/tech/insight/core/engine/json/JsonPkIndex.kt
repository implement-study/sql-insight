package tech.insight.core.engine.json

import tech.insight.core.bean.*
import tech.insight.core.environment.Session
import java.io.File
import java.nio.file.Files
import java.nio.file.Path

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

    override fun belongTo(): Table {
        return table
    }

    override fun find(session: Session): Cursor {
        val reader = Files.newBufferedReader(jsonFilePath)
        return JsonCursor(reader, session, this)
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
