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

package org.gongxuanzhang.mysql.constant;

/**
 * 默认大小长度
 *
 * @author gongxuanzhang
 */

public enum ConstantSize {
    PAGE("页", 16 * 1024),
    FILE_HEADER("文件头", 38, 0),
    PAGE_HEADER("页头", 56, FILE_HEADER.size),
    INFIMUM("下确界", 13, PAGE_HEADER.offset + PAGE_HEADER.size),
    SUPREMUM("上确界", 13, INFIMUM.size + INFIMUM.offset),
    SUPREMUM_BODY("上确界内容", 8),
    INFIMUM_BODY("下确界内容", 8),
    USER_RECORDS("用户数据初始", -1, SUPREMUM.size + SUPREMUM.offset),
    FILE_TRAILER("文件尾", 8),
    COMPACT_NULL("compact null值列表", 8),
    RECORD_HEADER("记录头", 5),
    INIT_PAGE_FREE_SPACE("刚初始化时的页的空闲空间",
            PAGE.size - FILE_HEADER.size - PAGE_HEADER.size - INFIMUM.size * 2 - FILE_TRAILER.size);

    private final String desc;
    private final int size;
    private final int offset;

    ConstantSize(String desc, int size, int offset) {
        this.desc = desc;
        this.size = size;
        this.offset = offset;
    }

    ConstantSize(String desc, int size) {
        this.desc = desc;
        this.size = size;
        this.offset = -1;
    }


    public String getDesc() {
        return desc;
    }

    public int getSize() {
        return size;
    }

    public int offset() {
        return this.offset;
    }

    public void checkSize(byte[] bytes) {
        if (bytes.length != this.getSize()) {
            throw new IllegalArgumentException(this.getDesc() + "大小必须是" + this.getSize() + "字节");
        }
    }

    public byte[] emptyBuff() {
        return new byte[this.size];
    }
}
