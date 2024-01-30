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
package tech.insight.core.environment

import tech.insight.core.annotation.Temporary
import tech.insight.core.bean.Table
import tech.insight.core.extension.toObject
import java.io.File
import java.io.FileInputStream

/**
 * @author gongxuanzhangmelt@gmail.com
 */
object TableLoader {
    private const val TABLE_SUFFIX = ".frm"
    fun loadTable(): List<Table> {
        val home = GlobalContext[DefaultProperty.DATA_DIR.key] ?: throw IllegalArgumentException()
        val dbArray = File(home).listFiles { obj: File -> obj.isDirectory() } ?: return emptyList()
        val tableList: MutableList<Table> = ArrayList()
        for (dbFile in dbArray) {
            val frmFileArray = dbFile.listFiles { f: File -> f.getName().endsWith(TABLE_SUFFIX) } ?: continue
            for (frmFile in frmFileArray) {
                tableList.add(loadTableMeta(frmFile))
            }
        }
        return tableList
    }

    @Temporary(detail = "temp use json parse")
    private fun loadTableMeta(frmFile: File): Table {
        FileInputStream(frmFile).use { fileInputStream ->
            return fileInputStream.toObject<Table>()
        }
    }
}
