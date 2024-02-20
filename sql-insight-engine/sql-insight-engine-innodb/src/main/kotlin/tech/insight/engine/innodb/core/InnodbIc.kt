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
package tech.insight.engine.innodb.core

import tech.insight.core.bean.Database
import tech.insight.core.bean.InsertRow
import tech.insight.core.bean.Table
import tech.insight.core.bean.value.ValueInt
import tech.insight.core.bean.value.ValueNull
import tech.insight.core.engine.AutoIncrementKeyCounter
import java.util.concurrent.atomic.AtomicLong

/**
 * @author gongxuanzhangmelt@gmail.com
 */
class InnodbIc(table: Table) : AutoIncrementKeyCounter {
    private val table: Table
    private val incrementColIndex: Int
    private val counter: AtomicLong

    init {
        this.table = table
        incrementColIndex = table.ext.autoColIndex
        //  todo
        counter = AtomicLong(0)
    }

    override fun dealAutoIncrement(row: InsertRow): Boolean {
        val targetValue = row.valueList[incrementColIndex]
        if (targetValue is ValueNull) {
            val valueInt = ValueInt(counter.incrementAndGet().toInt())
            row.valueList[incrementColIndex] = valueInt
            return true
        }
        val source: Int = (targetValue as ValueInt).source
        if (source > counter.get()) {
            counter.set(source.toLong())
        }
        return false
    }

    override fun reset(table: Table) {
        counter.set(0)
    }

    override fun reset(database: Database) {
        TODO("Not yet implemented")
    }

}
