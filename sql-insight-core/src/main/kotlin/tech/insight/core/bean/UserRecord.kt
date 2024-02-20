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
package tech.insight.core.bean

import org.gongxuanzhang.easybyte.core.ByteWrapper


/**
 * user record represents a physics row in disk.
 * different row format have different performance in storage.
 *
 * @author gongxuanzhangmelt@gmail.com
 */
interface UserRecord : ByteWrapper, Row {
    /**
     * the record to byte array.
     */
    fun rowBytes(): ByteArray

    /**
     * absolute offset in page， field not in page , application calculate
     */
    fun offset(): Int

    /**
     * [offset]
     * absolute offset in page don't in source.
     * require set up
     */
    fun setOffset(offset: Int)

    /**
     * next node relative offset
     */
    fun nextRecordOffset(): Int

    /**
     * @return user record delete sign
     */
    fun deleteSign(): Boolean

    /**
     * [rowBytes]
     */
    override fun toBytes(): ByteArray {
        return rowBytes()
    }
}
