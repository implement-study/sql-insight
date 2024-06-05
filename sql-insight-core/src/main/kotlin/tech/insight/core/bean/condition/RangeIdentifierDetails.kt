package tech.insight.core.bean.condition

import tech.insight.core.bean.value.Value
import tech.insight.core.engine.IdentifierSelectType


/**
 * @author gxz gongxuanzhangmelt@gmail.com
 **/
class RangeIdentifierDetails(override val name: String, val range: OpenEndRange<Value<*>>) : IdentifierDetails {


    override val selectType: IdentifierSelectType = IdentifierSelectType.MULTI_CONST

}
