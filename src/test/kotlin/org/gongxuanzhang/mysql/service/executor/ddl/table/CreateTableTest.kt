package org.gongxuanzhang.mysql.service.executor.ddl.table

import org.gongxuanzhang.mysql.doSql
import org.gongxuanzhang.mysql.entity.ColumnInfo
import org.gongxuanzhang.mysql.entity.ColumnType
import org.gongxuanzhang.mysql.tool.Context
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest


/**
 * @author gxz gongxuanzhangmelt@gmail.com
 **/
@SpringBootTest
class CreateTableTest {


    @Test
    fun createTable() {
        val database = "create_database"

        "create database $database".doSql()

        val tableName = "create_test_table_user"
        """
            create table $database.$tableName(
            id int primary key auto_increment,
            name varchar not null,
            gender varchar default '张三' not null,
            age int comment '年龄',
            id_card varchar UNIQUE,
            ) comment ='用户表'
        """.doSql()

        val select = Context.getTableManager().select("$database.$tableName")
        assert(select.database.databaseName == database)
        assert(select.tableName == tableName)
        assert(select.comment == "用户表")
        assert(select.columnInfos[0] == run {
            val columnInfo = ColumnInfo()
            columnInfo.name = "id"
            columnInfo.type = ColumnType.INT
            columnInfo.isAutoIncrement = true
            columnInfo
        })
        assert(select.columnInfos[1] == run {
            val columnInfo = ColumnInfo()
            columnInfo.name = "name"
            columnInfo.type = ColumnType.STRING
            columnInfo.isNotNull = true
            columnInfo
        })
        assert(select.columnInfos[2] == run {
            val columnInfo = ColumnInfo()
            columnInfo.name = "gender"
            columnInfo.type = ColumnType.STRING
            columnInfo.isNotNull = true
            columnInfo.defaultValue = "张三"
            columnInfo
        })
        assert(select.columnInfos[3] == run {
            val columnInfo = ColumnInfo()
            columnInfo.name = "age"
            columnInfo.type = ColumnType.INT
            columnInfo.comment = "年龄"
            columnInfo
        })
        assert(select.columnInfos[4] == run {
            val columnInfo = ColumnInfo()
            columnInfo.name = "id_card"
            columnInfo.type = ColumnType.STRING
            columnInfo.isUnique = true
            columnInfo
        })
        assert(select.comment == "用户表")
        assert(select.primaryKey == arrayListOf("id"))

        
        "drop database $database".doSql()

    }

}
