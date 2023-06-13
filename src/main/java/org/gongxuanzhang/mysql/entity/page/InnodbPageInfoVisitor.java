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

package org.gongxuanzhang.mysql.entity.page;

import org.gongxuanzhang.mysql.constant.ConstantSize;
import org.gongxuanzhang.mysql.entity.SelectRow;
import org.gongxuanzhang.mysql.entity.SelectRowImpl;
import org.gongxuanzhang.mysql.entity.TableInfo;

import java.util.ArrayList;
import java.util.List;

import static org.gongxuanzhang.mysql.tool.PageUtils.getUserRecordByOffset;

/**
 * @author gxz gongxuanzhang@foxmail.com
 **/
public class InnodbPageInfoVisitor {

    private final InnoDbPage page;

    private final TableInfo tableInfo;

    private List<SelectRow> selectRows;

    public InnodbPageInfoVisitor(InnoDbPage page) {
        this.page = page;
        this.tableInfo = page.getTableInfo();
    }


    /**
     * 是否是索引页
     **/
    public boolean isIndexPage() {
        return page.getFileHeader().getPageType() == PageType.FIL_PAGE_INODE.getValue();
    }

    /**
     * 是否是数据页
     **/
    public boolean isDataPage() {
        return page.getFileHeader().getPageType() == PageType.FIL_PAGE_INDEX.getValue();
    }


    public InnoDbPage getPage() {
        return page;
    }


    public List<SelectRow> showRows() {
        if (selectRows != null) {
            return selectRows;
        }
        selectRows = new ArrayList<>();
        short offset = (short) ConstantSize.INFIMUM.offset();
        while (true) {
            UserRecord userRecord = getUserRecordByOffset(page, offset);
            if (userRecord instanceof Supremum) {
                break;
            }
            offset = (short) userRecord.getRecordHeader().getNextRecordOffset();
            if (userRecord instanceof Compact) {
                SelectRowImpl selectRow = new SelectRowImpl(tableInfo, (Compact) userRecord);
                selectRows.add(selectRow);
            }
        }
        return selectRows;
    }
}
