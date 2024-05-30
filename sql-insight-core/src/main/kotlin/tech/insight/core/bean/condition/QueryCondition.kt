package tech.insight.core.bean.condition

import tech.insight.core.bean.Always
import tech.insight.core.bean.Limit
import tech.insight.core.bean.OrderBy
import tech.insight.core.bean.Where


/**
 * @author gxz gongxuanzhangmelt@gmail.com
 **/
class QueryCondition {

    var where: Where = Always

    var limit: Limit = Limit()

    var orderBy: OrderBy? = null

}
