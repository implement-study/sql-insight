package tech.insight.core.engine

import com.alibaba.druid.sql.SQLUtils
import com.alibaba.druid.sql.ast.statement.SQLCreateDatabaseStatement
import com.alibaba.druid.sql.ast.statement.SQLCreateTableStatement
import com.alibaba.druid.sql.ast.statement.SQLDeleteStatement
import com.alibaba.druid.sql.ast.statement.SQLDropDatabaseStatement
import com.alibaba.druid.sql.ast.statement.SQLDropTableStatement
import com.alibaba.druid.sql.ast.statement.SQLInsertStatement
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock
import com.alibaba.druid.sql.ast.statement.SQLUpdateStatement
import com.alibaba.druid.sql.ast.statement.SQLUseStatement
import com.alibaba.druid.sql.visitor.SQLASTVisitor
import tech.insight.core.command.Command
import tech.insight.core.command.CreateDatabase
import tech.insight.core.command.CreateTable
import tech.insight.core.command.DeleteCommand
import tech.insight.core.command.DropDatabase
import tech.insight.core.command.DropTable
import tech.insight.core.command.InsertCommand
import tech.insight.core.command.SelectCommand
import tech.insight.core.command.UnknownCommand
import tech.insight.core.command.UpdateCommand
import tech.insight.core.command.UseDatabaseCommand
import tech.insight.core.engine.filler.DispatcherFiller

/**
 * analysis sql to command
 *
 * @author gongxuanzhangmelt@gmail.com
 */
fun interface Analyzer {
    /**
     * analysis sql to a wrapped sql
     *
     * @return the command
     */
    fun analysisSql(sql: String): Command
}


/**
 * delegate to druid sql AST.
 * command type visitor wrap the sql to command type.
 * filler visitor fill the command field.
 *
 */
object DruidAnalyzer : Analyzer {

    override fun analysisSql(sql: String): Command {
        return CommandTypeVisitor(sql).command.apply {
            DispatcherFiller.fill(this)
        }
    }
}

class CommandTypeVisitor(private val sql: String) : SQLASTVisitor {

    private val sqlStatement = SQLUtils.parseSingleMysqlStatement(sql)

    var command: Command = UnknownCommand

    init {
        sqlStatement.accept(this)
        if (command is UnknownCommand) {
            throw UnsupportedOperationException("Unsupported sql type: $sql")
        }
    }

    override fun visit(x: SQLCreateDatabaseStatement): Boolean {
        this.command = CreateDatabase(sql, x)
        return false
    }

    override fun visit(x: SQLDropDatabaseStatement): Boolean {
        command = DropDatabase(sql, x)
        return false
    }

    override fun visit(x: SQLDeleteStatement): Boolean {
        command = DeleteCommand(sql, x)
        return false
    }

    override fun visit(x: SQLDropTableStatement): Boolean {
        command = DropTable(sql, x)
        return false
    }

    override fun visit(x: SQLCreateTableStatement): Boolean {
        command = CreateTable(sql, x)
        return false
    }

    override fun visit(x: SQLInsertStatement): Boolean {
        command = InsertCommand(sql, x)
        return false
    }

    override fun visit(x: SQLUpdateStatement): Boolean {
        command = UpdateCommand(sql, x)
        return false
    }

    override fun visit(x: SQLSelectQueryBlock): Boolean {
        command = SelectCommand(sql, x)
        return false
    }

    override fun visit(x: SQLUseStatement): Boolean {
        command = UseDatabaseCommand(sql, x)
        return false
    }
}


