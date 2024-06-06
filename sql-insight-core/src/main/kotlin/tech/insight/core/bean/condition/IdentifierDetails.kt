package tech.insight.core.bean.condition

import tech.insight.core.engine.IdentifierSelectType


/**
 * @author gxz gongxuanzhangmelt@gmail.com
 **/
interface IdentifierDetails : IdentifierDetailsGroup {

    val name: String

    val selectType: IdentifierSelectType


    override fun merge(identifierDetails: IdentifierDetails) {
        throw UnsupportedOperationException("identifierDetails can not merge identifierDetails")
    }

    override fun merge(otherGroup: IdentifierDetailsGroup) {
        throw UnsupportedOperationException("identifierDetails can not merge identifierDetailsGroup")
    }

}
