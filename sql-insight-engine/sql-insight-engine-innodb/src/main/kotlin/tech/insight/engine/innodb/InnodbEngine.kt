/*
 * Copyright 2023 java-mysql  and the original author or authors <gongxuanzhangmelt@gmail.com>.
 *
 * Licensed under the GNU Affero General Public License v3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://github.com/implement-study/sql-insight/blob/main/LICENSE
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package tech.insight.engine.innodb

import tech.insight.core.bean.InsertRow
import tech.insight.core.bean.Row
import tech.insight.core.bean.Table
import tech.insight.core.command.UpdateCommand
import tech.insight.core.engine.storage.StorageEngine
import tech.insight.core.extension.slf4j
import tech.insight.core.result.MessageResult
import tech.insight.core.result.ResultInterface
import tech.insight.engine.innodb.index.ClusteredIndex
import tech.insight.engine.innodb.page.InnoDbPage
import java.io.File
import java.io.IOException
import java.nio.file.Files

/**
 * @author gongxuanzhangmelt@gmail.com
 */
class InnodbEngine : StorageEngine {

    companion object {
        val log = slf4j<InnodbEngine>()
    }

    override val name: String = "innodb"

    override fun tableExtensions(): List<String> {
        return mutableListOf("ibd", "inf")
    }

    override fun openTable(table: Table) {
        if (table.indexList.isEmpty()) {
            val file = File(table.database.dbFolder, "${table.name}.inf")
            table.indexList.add(ClusteredIndex(table))
            try {
                val lines = Files.readAllLines(file.toPath())
                //  todo load index
            } catch (e: IOException) {
                throw RuntimeException(e)
            }
            for (index in table.indexList) {
                index.rndInit()
            }
        }
    }

    override fun createTable(table: Table): ResultInterface {
        val clusteredIndex = ClusteredIndex(table)
        val primaryFile: File = clusteredIndex.file
        if (!primaryFile.createNewFile()) {
            log.warn("{} already exists , execute create table will overwrite file", primaryFile.getAbsoluteFile())
        }
        val root = InnoDbPage.createRootPage(clusteredIndex)
        Files.write(primaryFile.toPath(), root.toBytes())
        log.info("create table {} with innodb,create ibd file", table.name)
        val file = File(table.database.dbFolder, "${table.name}.inf")
        if (file.createNewFile()) {
            return MessageResult("success create table ${table.name}")
        }
        return MessageResult("")

    }

    override fun truncateTable(table: Table): ResultInterface {
        TODO()
    }


    override fun update(oldRow: Row, update: UpdateCommand) {
        TODO("Not yet implemented")
    }

    override fun delete(deletedRow: Row) {
        TODO("Not yet implemented")
    }

    override fun refresh(table: Table) {
        TODO("Not yet implemented")
    }


    override fun insertRow(row: InsertRow) {
        val table: Table = row.belongTo()
        openTable(table)
        for (index in table.indexList) {
            index.insert(row)
        }
    }

}
