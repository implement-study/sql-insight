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
package tech.insight.core.bean

/**
 * visit a exprTableSource in order get target table info.
 * can't use for create table
 *
 * @author gongxuanzhangmelt@gmail.com
 */
class TableFillVisitor(private val tableContainer: TableContainer) : FillDataVisitor {
    override fun visit(x: SQLExprTableSource): Boolean {
        val tableVisitor = TableVisitor()
        x.accept(tableVisitor)
        tableContainer.setTable(tableVisitor.table)
        return true
    }
}
