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
import tech.insight.core.bean.desc.ColumnDesc
import tech.insight.core.bean.value.ValueVisitor

/**
 * @author gongxuanzhangmelt@gmail.com
 */
class ColumnFiller(val columnDesc: ColumnDesc) : BeanFiller<Column> {


    override fun endVisit(x: SQLColumnDefinition) {
        columnDesc.name = x.columnName
        columnDesc.autoIncrement = x.isAutoIncrement
        x.accept(ColConstraintVisitor())
        x.comment?.accept(CommentVisitor { columnDesc.comment = it })
        x.defaultExpr?.accept(ValueVisitor {
            columnDesc.defaultValue = it
        })
    }

    override fun endVisit(x: SQLDataType) {
        columnDesc.dataType = DataType.valueOf(x.name.uppercase(Locale.getDefault()))
    }

    override fun endVisit(x: SQLCharacterDataType) {
        columnDesc.dataType = DataType.valueOf(x.name.uppercase(Locale.getDefault()))
        columnDesc.length = x.length
    }

    inner class ColConstraintVisitor : SQLASTVisitor {
        override fun endVisit(x: SQLColumnUniqueKey) {
            columnDesc.unique = true
        }

        override fun endVisit(x: SQLNotNullConstraint) {
            columnDesc.notNull = true
        }

        override fun endVisit(x: SQLColumnPrimaryKey) {
            columnDesc.primaryKey = true
            columnDesc.unique = true
            columnDesc.notNull = true
        }
    }

}
