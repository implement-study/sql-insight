package tech.insight.core.plan

import java.io.File
import tech.insight.core.bean.Database
import tech.insight.core.command.CreateDatabase
import tech.insight.core.command.CreateTable
import tech.insight.core.environment.DatabaseManager
import tech.insight.core.environment.DefaultProperty
import tech.insight.core.environment.EngineManager
import tech.insight.core.environment.GlobalContext
import tech.insight.core.environment.TableLoader
import tech.insight.core.event.CreateDatabaseEvent
import tech.insight.core.event.CreateTableEvent
import tech.insight.core.event.EventPublisher
import tech.insight.core.exception.DatabaseExistsException
import tech.insight.core.exception.TableExistsException
import tech.insight.core.result.MessageResult
import tech.insight.core.result.ResultInterface


/**
 * @author gxz gongxuanzhangmelt@gmail.com
 **/
class CreateDatabasePlan(private val command: CreateDatabase) : DDLExecutionPlan(command) {

    override fun run(): ResultInterface {
        val home = GlobalContext[DefaultProperty.DATA_DIR]
        val dbFold = File(home, this.command.dbName)
        if (dbFold.exists() && !command.ifNotExists) {
            throw DatabaseExistsException(command.dbName)
        }
        if (dbFold.mkdirs()) {
            EventPublisher.publishEvent { CreateDatabaseEvent(Database(command.dbName)) }
            return MessageResult("create database [${command.dbName}]")
        }
        return MessageResult("skip the create because database ${command.dbName} exists")
    }

    override fun toString(): String {
        return command.toString()
    }
}

class CreateTablePlan(private val command: CreateTable) : DDLExecutionPlan(command) {
    private val tableDesc = command.tableDesc

    override fun run(): ResultInterface {
        tableDesc.checkMySelf()
        val dbFolder = DatabaseManager.require(tableDesc.databaseName!!).dbFolder
        val frmFile = dbFolder.resolve("${tableDesc.name}.frm")
        if (frmFile.createNewFile()) {
            val engine = EngineManager.selectEngine(tableDesc.engine)
            val table = tableDesc.build()
            engine.createTable(table)
            TableLoader.writeTableMeta(table)
            EventPublisher.publishEvent { CreateTableEvent(table) }
            return MessageResult("success create table ${tableDesc.name}")
        }
        if (!command.ifNotExists) {
            throw TableExistsException(tableDesc.name!!)
        }
        return MessageResult("skip the create because table ${tableDesc.name} exists")
    }


}
