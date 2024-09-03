package tech.insight.core.bean.condition

import tech.insight.core.bean.value.Value
import tech.insight.core.engine.IdentifierSelectType


/**
 * @author gxz gongxuanzhangmelt@gmail.com
 **/
class MultiConstIdentifierDetails(override val name: String, val points: List<Value<*>>) : IdentifierDetails {

    override val selectType: IdentifierSelectType = IdentifierSelectType.MULTI_CONST

    override fun get(identifierName: String): IdentifierDetails? {
        return if (name == identifierName) this else null
    }

    override fun iterator(): Iterator<IdentifierDetails> {
        return listOf(this).iterator()
    }

}
