/*
 * Copyright 2023 java-mysql  and the original author or authors <gongxuanzhangmelt@gmail.com>.
 *
 * Licensed under the GNU Affero General Public License v3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://github.com/implement-study/java-mysql/blob/main/LICENSE
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
    PAGE_SIZE("页", 16 * 1024),
    FILE_HEADER_SIZE("文件头", 38),
    PAGE_HEADER_SIZE("页头", 56),
    INFIMUM_SIZE("下确界", 13),
    SUPREMUM_SIZE("上确界", 13),
    SUPREMUM_BODY_SIZE("上确界内容", 8),
    INFIMUM_BODY_SIZE("下确界内容", 8),
    FILE_TRAILER("文件尾", 8),
    COMPACT_NULL_SIZE("compact null值列表", 8),
    RECORD_HEADER("记录头", 5);

    private final String desc;
    private final int size;

    ConstantSize(String desc, int size) {
        this.desc = desc;
        this.size = size;
    }

    public String getDesc() {
        return desc;
    }

    public int getSize() {
        return size;
    }

    public void checkSize(byte[] bytes) {
        if (bytes.length != this.getSize()) {
            throw new IllegalArgumentException(this.getDesc() + "大小必须是" + this.getSize() + "字节");
        }
    }
}
