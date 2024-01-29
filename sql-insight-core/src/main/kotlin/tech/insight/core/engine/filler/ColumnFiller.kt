package tech.insight.core.engine.filler

import com.alibaba.druid.sql.ast.statement.SQLColumnDefinition
import com.alibaba.druid.sql.ast.statement.SQLColumnPrimaryKey
import com.alibaba.druid.sql.ast.statement.SQLColumnUniqueKey
import com.alibaba.druid.sql.ast.statement.SQLNotNullConstraint
import com.alibaba.druid.sql.visitor.SQLASTVisitor
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
        col.dataType = DataType()
        x.accept(col.dataType)
        if (col.dataType.type === DataType.Type.VARCHAR) {
            col.variable = true
        }
        x.accept(ColConstraintVisitor())
        x.comment?.accept(CommentVisitor { col.comment = it })
        x.defaultExpr?.accept(ValueVisitor {
            col.defaultValue = it
        })
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
        }
    }

}
