package tech.insight.core.command

import com.alibaba.druid.sql.SQLUtils
import com.alibaba.druid.sql.ast.SQLObject
import com.alibaba.druid.sql.ast.SQLStatement
import com.alibaba.druid.sql.ast.statement.SQLCreateDatabaseStatement
import com.alibaba.druid.sql.ast.statement.SQLCreateTableStatement
import com.alibaba.druid.sql.ast.statement.SQLDropDatabaseStatement
import com.alibaba.druid.sql.ast.statement.SQLDropTableStatement
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock
import com.alibaba.druid.sql.ast.statement.SQLUseStatement
import tech.insight.core.bean.Always
import tech.insight.core.bean.Column
import tech.insight.core.bean.InsertRow
import tech.insight.core.bean.Table
import tech.insight.core.bean.Where
import tech.insight.core.bean.condition.Expression
import tech.insight.core.bean.condition.QueryCondition
import tech.insight.core.bean.desc.TableDesc
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
        return SQLUtils.formatMySql(sql)
    }
}

sealed class DMLCommand(override val sql: String, override val statement: SQLObject) : Command {
    lateinit var table: Table

    var isExplain = false

    override fun toString(): String {
        return truncateStringIfTooLong(SQLUtils.formatMySql(sql))
    }
}

sealed class DCLCommand(override val sql: String, override val statement: SQLObject) : Command {

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
    var tableDesc = TableDesc()
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

    val queryCondition = QueryCondition()

}


class UpdateCommand(sql: String, statement: SQLStatement) : DMLCommand(sql, statement) {

    val updateField: MutableMap<String, Expression> = HashMap()

    var where: Where = Always
}


class UseDatabaseCommand(sql: String, statement: SQLUseStatement) : DCLCommand(sql, statement) {

    lateinit var databaseName: String

}







