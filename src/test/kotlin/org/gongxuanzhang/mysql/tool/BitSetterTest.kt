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

package org.gongxuanzhang.mysql.tool

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test


/**
 * @author gxz gongxuanzhangmelt@gmail.com
 **/
class BitSetterTest {

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
    /**
     * @author gxz gongxuanzhang@foxmail.com
     */
    class BitSetterTest {
        @Test
        fun testSetBitToOne() {
            val originalByte: Byte = 0b00000000

            // 第 0 位修改为 1
            val modifiedByte1 = BitSetter.setBitToOne(originalByte, 0)
            assertEquals(0b00000001, modifiedByte1)

            // 第 3 位修改为 1
            val modifiedByte2 = BitSetter.setBitToOne(originalByte, 3)
            assertEquals(0b00001000, modifiedByte2)

            // 第 7 位修改为 1
            val modifiedByte3 = BitSetter.setBitToOne(originalByte, 7)
            assertEquals(0b10000000.toByte(), modifiedByte3)
        }

        @Test
        fun testSetBitToZero() {
            val originalByte: Byte = 0b01010101

            // 第 0 位修改为 0
            val modifiedByte1 = BitSetter.setBitToZero(originalByte, 0)
            assertEquals(0b01010100, modifiedByte1)

            // 第 2 位修改为 0
            val modifiedByte2 = BitSetter.setBitToZero(originalByte, 2)
            assertEquals(0b01010001, modifiedByte2)

            // 第 5 位修改为 0
            val modifiedByte3 = BitSetter.setBitToZero(originalByte, 4)
            assertEquals(0b01000101, modifiedByte3)
        }

    }
}
