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

package org.gongxuanzhang.sql.insight.core.engine.storage.innodb.page;

/**
 * default size
 *
 * @author gongxuanzhang
 */

public enum ConstantSize {
    PAGE(16 * 1024),
    FILE_HEADER(38, 0),
    PAGE_HEADER(56, FILE_HEADER.size),
    INFIMUM(13, PAGE_HEADER.offset + PAGE_HEADER.size),
    SUPREMUM(13, INFIMUM.size + INFIMUM.offset),
    SUPREMUM_BODY(8),
    INFIMUM_BODY(8),
    USER_RECORDS(-1, SUPREMUM.size + SUPREMUM.offset),
    FILE_TRAILER(8),
    COMPACT_NULL(8),
    RECORD_HEADER(5),
    INIT_PAGE_FREE_SPACE(
            PAGE.size - FILE_HEADER.size - PAGE_HEADER.size - INFIMUM.size * 2 - FILE_TRAILER.size - 4,
            SUPREMUM.offset() + SUPREMUM.getSize());

    private final int size;
    private final int offset;

    ConstantSize(int size, int offset) {
        this.size = size;
        this.offset = offset;
    }

    ConstantSize(int size) {
        this.size = size;
        this.offset = -1;
    }


    public int getSize() {
        return size;
    }

    public int offset() {
        return this.offset;
    }

    public void checkSize(byte[] bytes) {
        if (bytes.length != this.getSize()) {
            throw new IllegalArgumentException(this + "size must " + this.getSize() + "byte");
        }
    }

    public byte[] emptyBuff() {
        return new byte[this.size];
    }
}
