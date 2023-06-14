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

package org.gongxuanzhang.mysql.entity.page

import org.gongxuanzhang.mysql.chaosEquals
import org.junit.jupiter.api.Test


/**
 * @author gxz gongxuanzhangmelt@gmail.com
 **/
class CompactNullValueTest {

    @Test
    fun nullCol() {
        val compactNullValue = CompactNullValue(0x1234)
        val nullColIndex = compactNullValue.nullColIndex()
        assert(nullColIndex.chaosEquals(listOf(2, 4, 5, 9, 12)))
    }


    @Test
    fun setNull() {
        val nullValue = CompactNullValue()
        nullValue.setNull(1)
        var bytes = nullValue.toBytes()
        assert(bytes[0] == 0.toByte())
        assert(bytes[1] == 2.toByte())
        nullValue.setNull(15)
        bytes = nullValue.toBytes()
        assert(bytes[0] == (-128).toByte())
        assert(bytes[1] == 2.toByte())
    }
}
