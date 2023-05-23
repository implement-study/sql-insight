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


import lombok.Data;
import org.gongxuanzhang.mysql.constant.ConstantSize;
import org.gongxuanzhang.mysql.core.ByteBody;
import org.gongxuanzhang.mysql.core.ByteSwappable;
import org.gongxuanzhang.mysql.core.Refreshable;
import org.gongxuanzhang.mysql.entity.Cell;
import org.gongxuanzhang.mysql.entity.Column;
import org.gongxuanzhang.mysql.entity.InsertRow;
import org.gongxuanzhang.mysql.entity.ShowLength;
import org.gongxuanzhang.mysql.entity.TableInfo;
import org.gongxuanzhang.mysql.exception.MySQLException;

import java.nio.ByteBuffer;
import java.util.List;

/**
 * InnoDb 页结构
 * 默认16K 暂时不支持修改
 *
 * @author gxz gongxuanzhangmelt@gmail.com
 * @see InnoDbPageFactory
 **/
@Data
public class InnoDbPage implements ShowLength, ByteSwappable, Refreshable {


    /**
     * 文件头 38字节
     **/
    FileHeader fileHeader;
    /**
     * 页头 56字节
     **/
    PageHeader pageHeader;
    /**
     * 下确界，13字节
     **/
    Infimum infimum;
    /**
     * 上确界，13字节
     **/
    Supremum supremum;
    /**
     * 用户记录  不确定字节
     **/
    UserRecords userRecords;
    /**
     * 空闲空间，这里只记录字节数
     **/
    short freeSpace;
    /**
     * 页目录
     **/
    PageDirectory pageDirectory;
    /**
     * 文件尾 8字节
     **/
    FileTrailer fileTrailer;


    @Override
    public int length() {
        return ConstantSize.PAGE.getSize();
    }

    @Override
    public byte[] toBytes() {
        ByteBuffer buffer = ByteBuffer.allocate(length());
        buffer.put(fileHeader.toBytes());
        buffer.put(pageHeader.toBytes());
        buffer.put(infimum.toBytes());
        buffer.put(supremum.toBytes());
        buffer.put(userRecords.toBytes());
        buffer.position(buffer.position() + freeSpace);
        buffer.put(pageDirectory.toBytes());
        buffer.put(fileTrailer.toBytes());
        return buffer.array();
    }

    /**
     * 判断当前空闲空间是否足够
     *
     * @param length 需要的空间大小
     * @return true 是足够
     **/
    public boolean isEnough(int length) {
        return this.freeSpace >= length;
    }

    public void insert(InsertRow insertRow) throws MySQLException {
        Compact compact = new Compact();
        List<Cell<?>> cellList = insertRow.getCellList();
        TableInfo tableInfo = insertRow.getTableInfo();
        CompactNullValue compactNullValue = new CompactNullValue();
        ByteBody body = new ByteBody();
        for (int i = 0; i < cellList.size(); i++) {
            Column column = tableInfo.getColumns().get(i);
            Cell<?> cell = cellList.get(i);
            if (cell.getValue() == null) {
                compactNullValue.setNull(column.getNullIndex());
            }
            for (byte b : cell.toBytes()) {
                body.add(b);
            }
        }
        compact.setBody(body.toArray());
    }

    /**
     * 创建下一个记录头
     * @return 下一个记录头
     **/
    private RecordHeader createNextRecordHeader(){
        //  todo
        return null;
    }



    /**
     * 刷新表示整理数据头,比如页从数据页变成了目录页之后
     *
     **/
    @Override
    public void refresh() throws MySQLException {

    }
}
