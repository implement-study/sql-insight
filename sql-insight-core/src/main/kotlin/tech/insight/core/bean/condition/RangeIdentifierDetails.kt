package tech.insight.core.bean.condition

import tech.insight.core.engine.IdentifierSelectType


/**
 * @author gxz gongxuanzhangmelt@gmail.com
 **/
class RangeIdentifierDetails(override val name: String, val range: ExpressionRange) : IdentifierDetails {


    override val selectType: IdentifierSelectType = IdentifierSelectType.MULTI_CONST

}
