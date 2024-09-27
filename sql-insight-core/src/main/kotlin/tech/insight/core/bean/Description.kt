package tech.insight.core.bean


/**
 *
 * object description.
 * It is usually parsed through sql.
 *
 * @author gxz gongxuanzhangmelt@gmail.com
 **/
interface Description<T>  {

    /**
     * check desc is valid.if not throw exception.
     */
    fun checkMySelf()

    /**
     * build to object.
     */
    fun build(): T

}
