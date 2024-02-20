package tech.insight.engine.innodb.index

import org.gongxuanzhang.sql.insight.core.environment.SessionContext
import tech.insight.core.bean.Column
import tech.insight.core.bean.Table
import java.io.File

/**
 * second index
 *
 * @author gongxuanzhangmelt@gmail.com
 */
class SecondIndex protected constructor(table: Table) : InnodbIndex() {
    override fun rndInit() {}
    override val id: Int
        get() = 0

    override fun belongTo(): Table {
        return null
    }

    override fun find(sessionContext: SessionContext?): Cursor? {
        return null
    }

    override val name: String?
        get() = null

    override fun insert(row: InsertRow?) {}
    override val file: File?
        get() = null

    override fun columns(): List<Column> {
        return null
    }
}
