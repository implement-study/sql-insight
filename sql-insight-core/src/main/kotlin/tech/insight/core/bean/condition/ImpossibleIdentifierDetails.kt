package tech.insight.core.bean.condition

import tech.insight.core.engine.IdentifierSelectType


/**
 * @author gxz gongxuanzhangmelt@gmail.com
 **/
class ImpossibleIdentifierDetails(override val name: String) : IdentifierDetails {
    
    override val selectType: IdentifierSelectType = IdentifierSelectType.IMPOSSIBLE
        
}
