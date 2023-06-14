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

import org.gongxuanzhang.mysql.assertArrayEquals
import org.junit.jupiter.api.Test


/**
 * @author gxz gongxuanzhangmelt@gmail.com
 **/
class ArrayUtilsTest {

    @Test
    fun insertTest() {
        val shortArray = shortArrayOf(1, 2, 3, 4, 5)
        var result = ArrayUtils.insert(shortArray, 1, 1)

        assertArrayEquals(result, shortArrayOf(1, 1, 2, 3, 4, 5))

        result = ArrayUtils.insert(shortArray, 2, 1)

        assertArrayEquals(result, shortArrayOf(1, 2, 1, 3, 4, 5))


    }

}
