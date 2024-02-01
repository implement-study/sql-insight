package tech.insight.core


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
