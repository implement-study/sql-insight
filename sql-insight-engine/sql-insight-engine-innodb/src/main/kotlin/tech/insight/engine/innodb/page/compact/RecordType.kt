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
package tech.insight.engine.innodb.page.compact

import tech.insight.engine.innodb.page.initInfimumRecordHeader
import tech.insight.engine.innodb.page.initNormalRecordHeader
import tech.insight.engine.innodb.page.initPageRecordHeader
import tech.insight.engine.innodb.page.initSupremumRecordHeader

/**
 * Compact row format type
 *
 * @author gxz gongxuanzhang@foxmail.com
 */
enum class RecordType(val value: Int,val initByteArray: ByteArray) {
    /**
     * leaf-node
     */
    NORMAL(0x00,initNormalRecordHeader),

    /**
     * non-leaf-node(index)
     */
    PAGE(0x01,initPageRecordHeader),

    /**
     * infimum
     */
    INFIMUM(0x02,initInfimumRecordHeader),

    /**
     * supremum
     */
    SUPREMUM(0x03, initSupremumRecordHeader),

    /**
     * init unknown
     */
    UNKNOWN(Int.MAX_VALUE, TODO());


    companion object {
        fun valueOf(value: Int): RecordType {
            return entries.find { it.value == value }!!
        }
    }

}
