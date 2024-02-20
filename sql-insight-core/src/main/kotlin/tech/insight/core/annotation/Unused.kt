package tech.insight.core.annotation

/**
 * Indicates that a field is temporarily unavailable and is reserved only for program features
 *
 * @author gxz gongxuanzhang@foxmail.com
 */
@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.SOURCE)
annotation class Unused(val value: String = "")
