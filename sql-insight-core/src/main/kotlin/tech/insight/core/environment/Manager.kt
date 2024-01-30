package tech.insight.core.environment

import com.google.common.collect.HashBasedTable
import tech.insight.core.bean.Database
import tech.insight.core.bean.Table
import tech.insight.core.event.*
import tech.insight.core.extension.GuavaTable
import kotlin.collections.ArrayList

/**
 * delegate to guava [com.google.common.collect.Table]
 * row key is table database name
 * col key is table name
 * value is table info object
 *
 * @author gongxuanzhangmelt@gmail.com
 */
class TableDefinitionManager : MultipleEventListener {
    private val tableInfoCache: GuavaTable<String, String, Table> = HashBasedTable.create()
    fun load(table: Table) {
        tableInfoCache.put(table.databaseName, table.name, table)
    }

    fun unload(table: Table) {
        tableInfoCache.remove(table.databaseName, table.name)
    }

    fun unload(database: Database) {
        tableInfoCache.row(database.name).clear()
    }

    fun select(database: String, tableName: String): Table? {
        return tableInfoCache.get(database, tableName)
    }

    fun select(database: String): List<Table> {
        return ArrayList(tableInfoCache.row(database).values)
    }

    override fun onEvent(event: InsightEvent) {
        if (event is DropDatabaseEvent) {
            this.unload(event.database)
            return
        }
        if (event is CreateTableEvent) {
            load(event.table)
            return
        }
        if (event is DropTableEvent) {
            unload(event.table)
        }
    }

    override fun listenEvent(): List<Class<out InsightEvent>> {
        return listOf(DropDatabaseEvent::class.java, CreateTableEvent::class.java, DropTableEvent::class.java)
    }
}
