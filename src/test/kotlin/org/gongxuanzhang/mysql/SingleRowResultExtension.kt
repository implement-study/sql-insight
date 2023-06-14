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

package org.gongxuanzhang.mysql

import org.gongxuanzhang.mysql.core.result.SingleRowResult


fun SingleRowResult.destructuringEquals(other: List<String>?): Boolean {
    if (this.data.size != other?.size) {
        return false
    }
    val selectData = this.data.map { map -> map[this.head[0]] }

    return selectData.chaosEquals(other)
}

fun <E> List<E>.chaosEquals(other: List<String>): Boolean {
    if (this.size != other.size) {
        return false
    }
    for (i in 0..size) {
        if (this[i] != other[i]) {
            return false
        }
    }
    return true
}
