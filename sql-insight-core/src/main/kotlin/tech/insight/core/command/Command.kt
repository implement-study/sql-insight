package tech.insight.core.command

import com.alibaba.druid.sql.ast.SQLObject
import com.alibaba.druid.sql.ast.SQLStatement
import com.alibaba.druid.sql.ast.statement.*
import tech.insight.core.bean.*
import tech.insight.core.bean.condition.Expression
import tech.insight.core.util.truncateStringIfTooLong


/**
 * the command file sealed all of type command
 * @author gongxuanzhangmelt@gmail.com
 */
sealed interface Command {
    val statement: SQLObject
    val sql: String
}

data object UnknownCommand : Command {

    override val statement: SQLObject
        get() = TODO()

    override val sql: String
        get() = TODO()

    fun unsupported(): Nothing {
        throw UnsupportedOperationException("Unsupported command")
    }

}

sealed class DDLCommand(override val sql: String, override val statement: SQLStatement) : Command {
    override fun toString(): String {
        return sql
    }
}

sealed class DMLCommand(override val sql: String, override val statement: SQLObject) : Command {
    lateinit var table: Table

    var isExplain = false

    override fun toString(): String {
        return truncateStringIfTooLong(sql)
    }
}

sealed class CreateCommand(sql: String, statement: SQLStatement) : DDLCommand(sql, statement)
sealed class DropCommand(sql: String, statement: SQLStatement) : DDLCommand(sql, statement)
sealed class AlterCommand(sql: String, statement: SQLStatement) : DDLCommand(sql, statement)

class CreateDatabase(sql: String, statement: SQLCreateDatabaseStatement) : CreateCommand(sql, statement) {
    var ifNotExists = false
    lateinit var dbName: String

}

class CreateTable(sql: String, statement: SQLCreateTableStatement) : CreateCommand(sql, statement) {
    var ifNotExists = false
    lateinit var table: Table
}

class DropDatabase(sql: String, statement: SQLDropDatabaseStatement) : DropCommand(sql, statement) {
    var ifIsExists = false
    lateinit var databaseName: String
}

class DropTable(sql: String, statement: SQLDropTableStatement) : DropCommand(sql, statement) {
    val dropTables: MutableList<Table> = ArrayList()
    var ifExists = false
}


class DeleteCommand(sql: String, statement: SQLStatement) : DMLCommand(sql, statement) {

    var where: Where = Always
}

class InsertCommand(sql: String, statement: SQLStatement) : DMLCommand(sql, statement) {

    val insertColumns: MutableList<Column> = ArrayList()

    val insertRows: MutableList<InsertRow> = ArrayList()
}

class SelectCommand(sql: String, statement: SQLSelectQueryBlock) : DMLCommand(sql, statement) {

    var where: Where = Always

    lateinit var orderBy: OrderBy

    var limit: Limit = Limit()

}


class UpdateCommand(sql: String, statement: SQLStatement) : DMLCommand(sql, statement) {

    val updateField: MutableMap<String, Expression> = HashMap()

    var where: Where = Always
}









