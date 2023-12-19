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

package org.gongxuanzhang.sql.insight.core.engine.storage.innodb.page.compact;


import org.gongxuanzhang.easybyte.core.ByteWrapper;
import org.gongxuanzhang.sql.insight.core.engine.storage.innodb.page.PageObject;
import org.gongxuanzhang.sql.insight.core.object.Table;

/**
 * contains byte array.
 * per byte bit represent wh column value
 *
 * @author gongxuanzhang
 **/
public class CompactNullList implements ByteWrapper, PageObject {

    byte[] nullList;

    /**
     * the byte array is origin byte in page.
     * begin with right.
     * nullList length maybe 0
     **/
    public CompactNullList(byte[] nullList) {
        this.nullList = nullList;
    }

    public CompactNullList(Table table) {
        this(new byte[table.getExt().getNullableColCount() / Byte.SIZE]);
    }

    /**
     * @param index start 0
     **/
    public boolean isNull(int index) {
        int byteIndex = nullList.length - (index / Byte.SIZE) - 1;
        byte bitMap = this.nullList[byteIndex];
        int mask = 1 << (index % Byte.SIZE);
        return (mask & bitMap) == mask;
    }


    public void setNull(int index) {
        int byteIndex = nullList.length - (index / Byte.SIZE) - 1;
        byte mask = (byte) (1 << (index % Byte.SIZE));
        this.nullList[byteIndex] &= mask;
    }


    @Override
    public byte[] toBytes() {
        return this.nullList;
    }

    @Override
    public int length() {
        return this.nullList.length;
    }
}
