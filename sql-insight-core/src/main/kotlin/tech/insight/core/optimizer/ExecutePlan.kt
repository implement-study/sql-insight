package tech.insight.core.optimizer


/**
 * hand out to [StorageEngine] from [ExecuteEngine]
 * can also be executed directly from the execute engine
 *
 * @author gongxuanzhangmelt@gmail.com
 */
interface ExecutionPlan {
    val originalSql: String
}
