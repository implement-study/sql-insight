package tech.insight.share.data

import tech.insight.core.engine.SqlPipeline
import kotlin.random.Random


const val testDb = "test_db"
const val test_table = "test_table"

const val idCol = "id int primary key auto_increment"
const val nameCol = " name varchar not null"
const val genderCol = "gender varchar(20) default '男' not null comment '性别'"
const val idCardCol = " id_card char UNIQUE"
const val commentCol = "comment = '用户表'"

const val createTableIne =
    "create table IF NOT EXISTS $testDb.$test_table ($idCol,$nameCol,$genderCol,$idCardCol) $commentCol"
const val createTableDine = "create table  $testDb.$test_table ($idCol,$nameCol,$genderCol,$idCardCol) $commentCol"

const val createDatabase = "create database if not exists $testDb"

const val dropDatabaseDie = "drop database $testDb"

const val dropDatabaseIe = "drop database if exists $testDb"

const val dropTableDine = "drop table $testDb.$test_table"

const val dropTableIe = "drop table if exists $testDb.$test_table"


const val insert = "insert into $testDb.$test_table (id,name) values(1,'a'),(2,'b'),(null,'c'),(null,'b'),(null,'c')"

val largeValue = (1..1000).joinToString(",") { "('${StringGenerator.generatorRandomString(10)}')" }

val largeInsert = "insert into $testDb.$test_table (name) values $largeValue"

const val update = "update $testDb.$test_table set name = name + 'new name'"

const val updateWhere = "$update where id > 10"

const val deleteRemain1 = "delete from $testDb.$test_table where id>1"

const val deleteRemain100 = "delete from $testDb.$test_table where id>100"

const val select = "select * from $testDb.$test_table where id > 10 limit 4,10 order by id desc"

fun prepareDatabase() {
    SqlPipeline.doSql(createDatabase)
}

fun prepareDb() {
    SqlPipeline.doSql(createDatabase)
}

fun clearDatabase() {
    SqlPipeline.doSql(dropDatabaseIe)
}

fun prepareTable() {
    prepareDatabase()
    SqlPipeline.doSql(createTableIne)
}


object StringGenerator {
    fun generatorRandomString(length: Int): String {
        val charPool = ('a'..'z') + ('A'..'Z') + ('0'..'9')
        return (1..length)
            .map { Random.nextInt(0, charPool.size) }
            .map(charPool::get)
            .joinToString("")
    }
}
