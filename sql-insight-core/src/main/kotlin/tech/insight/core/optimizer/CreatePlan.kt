package tech.insight.core.optimizer

import tech.insight.core.annotation.Temporary
import tech.insight.core.bean.Database
import tech.insight.core.bean.Table
import tech.insight.core.command.CreateDatabase
import tech.insight.core.command.CreateTable
import tech.insight.core.environment.DefaultProperty
import tech.insight.core.environment.GlobalContext
import tech.insight.core.event.CreateDatabaseEvent
import tech.insight.core.event.CreateTableEvent
import tech.insight.core.event.EventPublisher
import tech.insight.core.exception.DatabaseExistsException
import tech.insight.core.exception.DatabaseNotExistsException
import tech.insight.core.exception.TableExistsException
import tech.insight.core.extension.json
import tech.insight.core.result.MessageResultInterface
import tech.insight.core.result.ResultInterface
import java.io.File
import java.nio.file.Files


/**
 * @author gxz gongxuanzhangmelt@gmail.com
 **/
class CreateDatabasePlan(private val command: CreateDatabase) : DDLExecutionPlan(command) {
    override val originalSql: String
        get() = command.sql

    override fun run(): ResultInterface {
        val home = GlobalContext[DefaultProperty.DATA_DIR]
        val dbFold = File(home, this.command.dbName)
        if (dbFold.exists() && !command.ifNotExists) {
            throw DatabaseExistsException(command.dbName)
        }
        if (dbFold.mkdirs()) {
            EventPublisher.publishEvent { CreateDatabaseEvent(Database(command.dbName)) }
            return MessageResultInterface("create database [${command.dbName}]")
        }
        return MessageResultInterface("skip the create because database ${command.dbName} exists")
    }
}

class CreateTablePlan(private val command: CreateTable) : DDLExecutionPlan(command) {
    private val table: Table = command.table
    override val originalSql: String
        get() = command.sql

    override fun run(): ResultInterface {
        val dbFolder: File = table.database.dbFolder
        if (!dbFolder.exists() || !dbFolder.isDirectory()) {
            throw DatabaseNotExistsException(table.databaseName)
        }
        val frmFile = File(dbFolder, "${table.name}.frm")
        if (frmFile.createNewFile()) {
            Files.write(frmFile.toPath(), tableFrmByteArray())
            table.engine.createTable(table)
            EventPublisher.publishEvent { CreateTableEvent(table) }
            return MessageResultInterface("success create table ${table.name}")
        }
        if (!command.ifNotExists) {
            throw TableExistsException(table)
        }
        return MessageResultInterface("skip the create because table ${table.name} exists")
    }

    @Temporary(detail = "use json temp")
    private fun tableFrmByteArray(): ByteArray {
        return table.json().toByteArray()
    }
}
