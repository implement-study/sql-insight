package tech.insight.core.engine

import com.alibaba.druid.sql.SQLUtils
import com.alibaba.druid.sql.ast.statement.*
import com.alibaba.druid.sql.visitor.SQLASTVisitor
import tech.insight.core.command.*
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
    lateinit var command: Command

    init {
        sqlStatement.accept(this)
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
}


