package tech.insight.core.bean.condition

import tech.insight.core.engine.IdentifierSelectType


/**
 * @author gxz gongxuanzhangmelt@gmail.com
 **/
class ImpossibleIdentifierDetails(override val name: String) : IdentifierDetails {
    
    override val selectType: IdentifierSelectType = IdentifierSelectType.IMPOSSIBLE
    
    override fun get(identifierName: String): IdentifierDetails? {
        return if (name == identifierName) this else null
    }

    override fun iterator(): Iterator<IdentifierDetails> {
        return listOf(this).iterator()
    }

}
