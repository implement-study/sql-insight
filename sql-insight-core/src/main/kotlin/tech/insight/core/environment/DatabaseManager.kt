package tech.insight.core.environment

import tech.insight.core.bean.Database
import tech.insight.core.event.CreateDatabaseEvent
import tech.insight.core.event.DropDatabaseEvent
import tech.insight.core.event.InsightEvent
import tech.insight.core.event.MultipleEventListener
import tech.insight.core.exception.DatabaseNotExistsException
import tech.insight.core.logging.TimeReport.timeReport
import java.util.concurrent.ConcurrentHashMap

/**
 * database manager
 * @author gongxuanzhangmelt@gmail.com
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
