package tech.insight.core.bean.condition

import tech.insight.core.bean.Row
import tech.insight.core.bean.value.Value
import tech.insight.core.bean.value.ValueInt
import tech.insight.core.bean.value.ValueVarchar


/**
 * @author gxz gongxuanzhangmelt@gmail.com
 **/
class IntExpression(private val value: Int) : Expression {
    override fun getExpressionValue(row: Row): ValueInt {
        return ValueInt(value)
    }

    override val identifiers = emptyList<String>()
    
}

class StringExpression(private val value: String) : Expression {

    override val identifiers = emptyList<String>()
    
    override fun getExpressionValue(row: Row): ValueVarchar {
        return ValueVarchar(value)
    }
}

class IdentifierExpression(private val name: String) : Expression {

    override val identifiers = listOf(name)
    
    override fun getExpressionValue(row: Row): Value<*> {
        return row.getValueByColumnName(name)
    }
}
