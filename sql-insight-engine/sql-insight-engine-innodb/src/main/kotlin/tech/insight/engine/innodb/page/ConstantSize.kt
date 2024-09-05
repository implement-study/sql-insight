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
package tech.insight.engine.innodb.page

/**
 * default size
 *
 * @author gongxuanzhang
 */
enum class ConstantSize {
    PAGE(16 * 1024),
    RECORD_HEADER(5),
    FILE_HEADER(38, 0),
    PAGE_HEADER(56, FILE_HEADER.size),
    INFIMUM(13, FILE_HEADER.size + PAGE_HEADER.size),
    SUPREMUM(13, FILE_HEADER.size + PAGE_HEADER.size + INFIMUM.size),
    SUPREMUM_BODY(8),
    INFIMUM_BODY(8),
    USER_RECORDS(-1, SUPREMUM.offset + SUPREMUM_BODY.size),
    FILE_TRAILER(8, PAGE.size - 8),
    COMPACT_NULL(8);

    val size: Int
    val offset: Int

    constructor(size: Int, offset: Int) {
        this.size = size
        this.offset = offset
    }

    constructor(size: Int) {
        this.size = size
        offset = -1
    }

    fun checkSize(bytes: ByteArray) {
        require(bytes.size == size) { this.toString() + " size must " + size + "byte" }
    }

    fun emptyBuff(): ByteArray {
        return ByteArray(size)
    }
}
