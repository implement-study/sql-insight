@file:Suppress("UNCHECKED_CAST")

package tech.insight.core.environment

import com.google.common.collect.HashBasedTable
import tech.insight.core.bean.Database
import tech.insight.core.bean.Table
import tech.insight.core.engine.storage.StorageEngine
import tech.insight.core.event.*
import tech.insight.core.event.EventListener
import tech.insight.core.exception.DatabaseNotExistsException
import tech.insight.core.exception.DuplicationEngineNameException
import tech.insight.core.exception.EngineNotFoundException
import tech.insight.core.exception.TableNotExistsException
import tech.insight.core.extension.GuavaTable
import tech.insight.core.extension.timeReport
import tech.insight.core.logging.Logging
import java.util.*
import java.util.concurrent.ConcurrentHashMap

/**
 * delegate to guava [com.google.common.collect.Table]
 * row key is table database name
 * col key is table name
 * value is table info object
 *
 * @author gongxuanzhangmelt@gmail.com
 */
object TableManager : Logging(), MultipleEventListener {

    private val tableInfoCache: GuavaTable<String, String, Table> = HashBasedTable.create()

    init {
        timeReport("init table manager") {
            TableLoader.loadTable().forEach { this.load(it) }
        }
    }

    private fun load(table: Table) {
        tableInfoCache.put(table.databaseName, table.name, table)
    }

    private fun unload(table: Table) {
        tableInfoCache.remove(table.databaseName, table.name)
    }

    private fun unload(database: Database) {
        tableInfoCache.row(database.name).clear()
    }

    fun select(database: String, tableName: String): Table? {
        return tableInfoCache[database, tableName]
    }

    fun require(database: String, tableName: String): Table {
        return tableInfoCache[database, tableName] ?: throw TableNotExistsException("$database $tableName")
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

/**
 * database manager
 */
object DatabaseManager : MultipleEventListener {
    private val databaseCache: MutableMap<String, Database> = ConcurrentHashMap()

    init {
        timeReport("init database Manager") {
            DatabaseLoader.loadDatabase().forEach { this.load(it) }
        }
    }

    private fun load(database: Database) {
        databaseCache[database.name] = database
    }

    private fun unload(database: Database) {
        databaseCache.remove(database.name)
    }

    /**
     * select maybe return null
     */
    fun select(databaseName: String): Database? {
        return databaseCache[databaseName]
    }

    fun require(databaseName: String): Database {
        return select(databaseName) ?: throw DatabaseNotExistsException(databaseName)
    }


    override fun onEvent(event: InsightEvent) {
        if (event is DropDatabaseEvent) {
            this.unload(event.database)
            return
        }
        if (event is CreateDatabaseEvent) {
            load(event.database)
            return
        }
    }

    override fun listenEvent(): List<Class<out InsightEvent>> {
        return listOf(DropDatabaseEvent::class.java, CreateDatabaseEvent::class.java)
    }
}


object EngineManager : Logging(), StorageEngineManager {
    private val storageEngineMap: MutableMap<String, StorageEngine> = ConcurrentHashMap()

    init {
        EngineLoader.loadEngine().forEach { registerEngine(it) }
    }

    override fun allEngine(): List<StorageEngine> {
        return ArrayList(storageEngineMap.values)
    }

    override fun registerEngine(engine: StorageEngine) {
        info("register engine [{}], class {}", engine.name, engine.javaClass.getName())
        if (storageEngineMap.putIfAbsent(engine.name.uppercase(Locale.getDefault()), engine) != null) {
            throw DuplicationEngineNameException("engine ${engine.name} already register ")
        }
        if (engine is MultipleEventListener) {
            EventPublisher.registerMultipleListener(engine)
        } else if (engine is EventListener<*>) {
            EventPublisher.registerListener(engine as EventListener<in InsightEvent>)
        }
    }


    override fun selectEngine(engineName: String?): StorageEngine {
        val finalEngineName = engineName ?: GlobalContext[DefaultProperty.DEFAULT_ENGINE]
        return storageEngineMap[finalEngineName.uppercase(Locale.getDefault())] ?: throw EngineNotFoundException(
            finalEngineName
        )
    }
}


