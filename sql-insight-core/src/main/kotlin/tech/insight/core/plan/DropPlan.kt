package tech.insight.core.plan

import tech.insight.core.bean.Table
import tech.insight.core.command.DropDatabase
import tech.insight.core.command.DropTable
import tech.insight.core.environment.DatabaseManager
import tech.insight.core.environment.DefaultProperty
import tech.insight.core.environment.GlobalContext
import tech.insight.core.event.DropDatabaseEvent
import tech.insight.core.event.DropTableEvent
import tech.insight.core.event.EventPublisher
import tech.insight.core.exception.DatabaseNotExistsException
import tech.insight.core.exception.TableNotExistsException
import tech.insight.core.result.MessageResult
import tech.insight.core.result.ResultInterface
import java.io.File
import java.nio.file.Files
import kotlin.io.path.ExperimentalPathApi
import kotlin.io.path.deleteRecursively


/**
 * @author gxz gongxuanzhangmelt@gmail.com
 **/
class DropDatabasePlan(private val command: DropDatabase) : DDLExecutionPlan(command) {

    @OptIn(ExperimentalPathApi::class)
    override fun run(): ResultInterface {
        val home = GlobalContext[DefaultProperty.DATA_DIR]
        val dbFold = File(home, this.command.databaseName)
        if (dbFold.exists()) {
            dbFold.toPath().deleteRecursively()
            val database = DatabaseManager.require(this.command.databaseName)
            EventPublisher.publishEvent {
                DropDatabaseEvent(database)
            }
            return MessageResult("drop ${command.databaseName}")
        }
        if (!command.ifIsExists) {
            throw DatabaseNotExistsException(this.command.databaseName)
        }
        return MessageResult("skip drop ${command.databaseName} because database not exists")
    }


    override fun toString(): String {
        return "DropDatabase[${command.databaseName}]"

    }

}

class DropTablePlan(private val command: DropTable) : DDLExecutionPlan(command) {
    private val dropTables = command.dropTables

    override fun run(): ResultInterface {
        val skipTableNames = mutableListOf<String>()
        val dropTableNames = mutableListOf<String>()
        this.dropTables.forEach { table: Table ->
            val dbFolder: File = table.database.dbFolder
            val frmFile = File(dbFolder, "${table.name}.frm")
            if (Files.deleteIfExists(frmFile.toPath())) {
                for (ext in table.engine.tableExtensions()) {
                    Files.deleteIfExists(File(dbFolder, "${table.name}.$ext").toPath())
                }
                EventPublisher.publishEvent { DropTableEvent(table) }
                dropTableNames.add(table.name)
                return@forEach
            }
            if (this.command.ifExists) {
                skipTableNames.add(table.name)
            } else {
                throw TableNotExistsException(table)
            }
        }
        return MessageResult("finish drop ${dropTableNames.size} tables $dropTableNames,skip ${skipTableNames.size} tables $skipTableNames")
    }

    override fun toString(): String {
        return "DropTable[${command.dropTables.joinToString { it.name }}]"
    }

}
