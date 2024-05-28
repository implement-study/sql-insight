package tech.insight.core.exception


/**
 * @author gxz gongxuanzhangmelt@gmail.com
 **/
abstract class DatabaseException : SqlInsightException()

class DatabaseExistsException(dbName: String) : SqlInsightException("database $dbName already exists")

class DatabaseNotExistsException(dbName: String) : SqlInsightException("database $dbName not exists")

class DatabaseNotSelectedException : SqlInsightException("database not selected")
