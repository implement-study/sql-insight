package tech.insight.core.engine

import tech.insight.core.annotation.Temporary


/**
 *
 * identifier select type
 *
 * @author gxz gongxuanzhangmelt@gmail.com
 **/
enum class IdentifierSelectType {
    /**
     * a = 1
     */
    CONST,

    /**
     * a = 1 or a = 2
     */
    MULTI_CONST,

    /**
     * a > 1
     */
    RANGE,

    /**
     * a = 2 and a = 3
     */
    IMPOSSIBLE,

    /**
     * a  = 1 or a > 5
     */
    @Temporary
    COMPLEX


}
