package tech.insight.core.engine.filler

import com.alibaba.druid.sql.ast.SQLDataType
import com.alibaba.druid.sql.ast.statement.SQLCharacterDataType
import com.alibaba.druid.sql.ast.statement.SQLColumnDefinition
import com.alibaba.druid.sql.ast.statement.SQLColumnPrimaryKey
import com.alibaba.druid.sql.ast.statement.SQLColumnUniqueKey
import com.alibaba.druid.sql.ast.statement.SQLNotNullConstraint
import com.alibaba.druid.sql.visitor.SQLASTVisitor
import java.util.*
import tech.insight.core.bean.Column
import tech.insight.core.bean.DataType
import tech.insight.core.bean.value.ValueVisitor

/**
 * @author gongxuanzhangmelt@gmail.com
 */
class ColumnFiller(val col: Column) : BeanFiller<Column> {


    override fun endVisit(x: SQLColumnDefinition) {
        col.name = x.columnName
        col.autoIncrement = x.isAutoIncrement
        x.accept(ColConstraintVisitor())
        x.comment?.accept(CommentVisitor { col.comment = it })
        x.defaultExpr?.accept(ValueVisitor {
            col.defaultValue = it
        })
    }

    override fun endVisit(x: SQLDataType) {
        col.dataType = DataType.valueOf(x.name.uppercase(Locale.getDefault()))
        if (col.dataType == DataType.VARCHAR) {
            col.variable = true
        }
        col.length = col.dataType.defaultLength
    }

    override fun endVisit(x: SQLCharacterDataType) {
        col.dataType = DataType.valueOf(x.name.uppercase(Locale.getDefault()))
        if (col.dataType == DataType.VARCHAR) {
            col.variable = true
        }
        col.length = x.length
        if (col.length < 0) {
            col.length = col.dataType.defaultLength
        }
    }

    inner class ColConstraintVisitor : SQLASTVisitor {
        override fun endVisit(x: SQLColumnUniqueKey) {
            col.unique = true
        }

        override fun endVisit(x: SQLNotNullConstraint) {
            col.notNull = true
        }

        override fun endVisit(x: SQLColumnPrimaryKey) {
            col.primaryKey = true
            col.unique = true
            col.notNull = true
        }
    }

}
