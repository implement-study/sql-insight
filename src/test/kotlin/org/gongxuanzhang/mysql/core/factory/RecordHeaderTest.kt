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

package org.gongxuanzhang.mysql.core.factory

import org.gongxuanzhang.mysql.entity.page.RecordHeader
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test


/**
 * @author gxz gongxuanzhangmelt@gmail.com
 **/
class RecordHeaderTest {


    @Test
    fun setOwn() {
        val recordHeader = RecordHeader()
        recordHeader.setnOwned(8)
        assert(recordHeader.source[0] == 0x08.toByte())
    }

    @Test
    fun testSetHeapNo() {
        val recordHead = RecordHeader()
        val max = (1 shl 13) - 1
        for (expect in 1..max) {
            recordHead.heapNo = expect
            val high = recordHead.source[1].toInt() and 0xff
            val low = recordHead.source[2].toInt() and 0xff
            val actual = (high shl 5) or (low ushr 3)
            assertEquals(expect, actual)
        }
    }


}

