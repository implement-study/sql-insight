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
 * @author gxz gongxuanzhang@foxmail.com
 **/
public enum PageType {
    /**
     * log page
     **/
    FIL_PAGE_TYPE_UNDO_LOG((short) 0X0001),
    /**
     * data page
     **/
    FIL_PAGE_INDEX((short) 0X45bf),
    /**
     * index page
     **/
    FIL_PAGE_INODE((short) 0x0003);


    private final short value;

    PageType(short value) {
        this.value = value;
    }

    public short getValue() {
        return value;
    }

    static PageType valueOf(int value) {
        for (PageType pageType : values()) {
            if (pageType.getValue() == value) {
                return pageType;
            }
        }
        throw new IllegalArgumentException("page type error value[ " + value + " ]");
    }
}

