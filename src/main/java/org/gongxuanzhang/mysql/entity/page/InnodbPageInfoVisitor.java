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

package org.gongxuanzhang.mysql.entity.page;

import org.gongxuanzhang.mysql.constant.ConstantSize;
import org.gongxuanzhang.mysql.entity.PrimaryKey;
import org.gongxuanzhang.mysql.entity.SelectRow;
import org.gongxuanzhang.mysql.entity.SelectRowImpl;
import org.gongxuanzhang.mysql.entity.TableInfo;
import org.gongxuanzhang.mysql.exception.MySQLException;
import org.gongxuanzhang.mysql.tool.PageReader;

import java.util.ArrayList;
import java.util.List;

import static org.gongxuanzhang.mysql.tool.PageUtils.getUserRecordByOffset;

/**
 * @author gxz gongxuanzhang@foxmail.com
 **/
public class InnodbPageInfoVisitor {

    private InnoDbPage page;

    private final TableInfo tableInfo;

    private List<SelectRow> selectRows;

    public InnodbPageInfoVisitor(InnoDbPage page) {
        this.page = page;
        this.tableInfo = page.getTableInfo();
    }

    public InnodbPageInfoVisitor(byte[] pageBuffer) {
        this(InnoDbPageFactory.getInstance().swap(pageBuffer));
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

    /**
     * 返回下一页，如果是数据页返回下一页 如果是目录页返回第一个数据页
     * 同时修改持有的page
     *
     * @return 可能为null 表示没有下一页
     **/
    public InnoDbPage nextPage() throws MySQLException {
        if (this.page.getFileHeader().next == 0) {
            return null;
        }
        this.page = PageReader.readInnodbPage(this.tableInfo.dataFile(), this.page.getFileHeader().next);
        return this.page;
    }


    /**
     * 索引页找到主键最终落在哪个页
     *
     * @param targetKey 目标key
     * @return 返回页的偏移量
     **/
    public int binarySearchSlotIndex(PrimaryKey targetKey) throws MySQLException {
        if (!this.isIndexPage()) {
            throw new IllegalStateException("当前页不是索引页");
        }
        short[] slots = this.page.pageDirectory.getSlots();
        IndexRecordFactory recordFactory = new IndexRecordFactory();
        int left = 0;
        int right = slots.length - 1;
        while (right - left <= 1) {
            int mid = (right + left) >> 1;
            short offset = slots[mid];
            Index index = recordFactory.swap(this.page, offset);
            PrimaryKey baseKey = index.getPrimaryKey(this.tableInfo);
            int compare = targetKey.compareTo(baseKey);
            if (compare == 0) {
                return index.getPageOffset();
            }
            if (compare < 0) {
                right = mid - 1;
            } else {
                left = mid + 1;
            }
        }
        Index rightIndex = recordFactory.swap(this.page, slots[right]);
        if (targetKey.compareTo(rightIndex.getPrimaryKey(this.tableInfo)) >= 0) {
            return slots[right];
        }
        return slots[left];
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
