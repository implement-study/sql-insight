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
package tech.insight.engine.innodb.utils

import org.gongxuanzhang.sql.insight.core.`object`.Table
import tech.insight.core.bean.value.Value
import java.util.function.Function

/**
 * @author gongxuanzhangmelt@gmail.com
 */
object RowComparator {
    fun primaryKeyComparator(): Comparator<InnodbUserRecord> {
        return baseComparator().thenComparing(Function<InnodbUserRecord, Value<Any>> { innodbUserRecord: InnodbUserRecord ->
            val table: Table = innodbUserRecord.belongTo()
            val primaryKeyName: String = table.getExt().getPrimaryKeyName()
            innodbUserRecord.getValueByColumnName(primaryKeyName)
        })
    }

    fun baseComparator(): Comparator<InnodbUserRecord> {
        return Comparator<InnodbUserRecord> { record1: InnodbUserRecord?, record2: InnodbUserRecord? ->
            if (record1 is Supremum) {
                return@Comparator 1
            }
            if (record2 is Supremum) {
                return@Comparator -1
            }
            if (record1 is Infimum) {
                return@Comparator -1
            }
            if (record2 is Infimum) {
                return@Comparator 1
            }
            0
        }
    }
}
