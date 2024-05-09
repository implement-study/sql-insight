/*
 * Copyright 2023 sql-insight  and the original author or authors <gongxuanzhangmelt@gmail.com>.
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
 * @author gongxuanzhangmelt@gmail.com
 */
class OrderBy(private val column: Array<String>, private val asc: BooleanArray) : Comparator<Row> {
    private val comparator: Comparator<Row>

    init {
        comparator = createComparator()
    }

    private fun createComparator(): Comparator<Row> {
        return Comparator { r1: Row, r2: Row ->
            for (i in column.indices) {
                val col = column[i]
                val `val`: Int = r1.getValueByColumnName(col).compareTo(r2.getValueByColumnName(col))
                if (`val` != 0) {
                    return@Comparator if (asc[i]) `val` else -`val`
                }
            }
            0
        }
    }

    override fun compare(o1: Row, o2: Row): Int {
        return comparator.compare(o1, o2)
    }
}
