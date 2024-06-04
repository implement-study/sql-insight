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

    override fun identifiers(): Set<String> {
        return emptySet()
    }

    override fun originExpressionString(): String {
        return "$value"
    }


}

class StringExpression(private val value: String) : Expression {


    override fun getExpressionValue(row: Row): ValueVarchar {
        return ValueVarchar(value)
    }

    override fun identifiers(): Set<String> {
        return emptySet()
    }

    override fun originExpressionString(): String {
        return "'$value'"
    }
}

class IdentifierExpression(private val name: String) : Expression {

    override fun getExpressionValue(row: Row): Value<*> {
        return row.getValueByColumnName(name)
    }

    override fun identifiers(): Set<String> {
        return setOf(name)
    }

    override fun originExpressionString(): String {
        return name
    }
}
