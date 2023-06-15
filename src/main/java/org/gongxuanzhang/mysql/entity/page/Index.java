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

import org.gongxuanzhang.mysql.annotation.NotInPage;
import org.gongxuanzhang.mysql.entity.Column;
import org.gongxuanzhang.mysql.entity.IntegerPrimaryKey;
import org.gongxuanzhang.mysql.entity.PrimaryKey;
import org.gongxuanzhang.mysql.entity.TableInfo;
import org.gongxuanzhang.mysql.entity.VarcharPrimaryKey;
import org.gongxuanzhang.mysql.tool.BitUtils;

import java.nio.ByteBuffer;

/**
 * 索引行格式
 *
 * @author gxz gongxuanzhangmelt@gmail.com
 **/
public class Index implements UserRecord {


    RecordHeader recordHeader;

    /**
     * 索引长度
     **/
    short indexLength;

    /**
     * 索引内容
     **/
    byte[] indexBody;


    /**
     * 链接的具体数据页在文件的偏移量
     **/
    int dataPageOffset;

    /**
     * 这一页对应的偏移量
     **/
    @NotInPage("表示记录在页中的偏移量，并不在页中真实存储")
    int pageOffset;

    //  不提供get方法 通过HavePrimaryKey接口调用

    @NotInPage("主键是计算出来显示的")
    private PrimaryKey primaryKey;


    @Override
    public byte[] toBytes() {
        ByteBuffer byteBuffer = ByteBuffer.allocate(this.length());
        byteBuffer.put(recordHeader.toBytes());
        byteBuffer.putShort(indexLength);
        byteBuffer.put(indexBody);
        byteBuffer.putInt(this.dataPageOffset);
        return byteBuffer.array();
    }

    @Override
    public RecordHeader getRecordHeader() {
        return this.recordHeader;
    }

    @Override
    public int pageOffset() {
        return this.pageOffset;
    }

    public Index setPageOffset(int pageOffset) {
        this.pageOffset = pageOffset;
        return this;
    }

    @Override
    public int length() {
        return this.recordHeader.length() +
                // 索引长度
                2 +
                indexBody.length +
                //  数据页偏移量
                4;
    }

    @Override
    public PrimaryKey getPrimaryKey(TableInfo tableInfo) {
        if (this.primaryKey != null) {
            return this.primaryKey;
        }
        this.primaryKey = calculatePrimaryKey(tableInfo);
        return primaryKey;
    }

    private PrimaryKey calculatePrimaryKey(TableInfo tableInfo) {
        int[] primaryKeyIndex = tableInfo.getPrimaryKeyIndex();
        if (primaryKeyIndex.length == 0) {
            throw new UnsupportedOperationException("暂不支持无主键");
        }
        if (primaryKeyIndex.length == 1) {
            Column primaryCol = tableInfo.getColumns().get(0);
            switch (primaryCol.getType()) {
                case INT:
                    return new IntegerPrimaryKey(BitUtils.joinInt(this.indexBody));
                case VARCHAR:
                    return new VarcharPrimaryKey(new String(this.indexBody));
                default:
                    throw new UnsupportedOperationException("不支持" + primaryCol.getType() + "类型主键");
            }
        } else {
            throw new UnsupportedOperationException("暂不支持联合主键");
        }
    }



    public Index setRecordHeader(RecordHeader recordHeader) {
        this.recordHeader = recordHeader;
        return this;
    }

    public short getIndexLength() {
        return indexLength;
    }

    public Index setIndexLength(short indexLength) {
        this.indexLength = indexLength;
        return this;
    }

    public byte[] getIndexBody() {
        return indexBody;
    }

    public Index setIndexBody(byte[] indexBody) {
        this.indexBody = indexBody;
        return this;
    }

    public int getDataPageOffset() {
        return dataPageOffset;
    }

    public Index setDataPageOffset(int dataPageOffset) {
        this.dataPageOffset = dataPageOffset;
        return this;
    }

    public int getPageOffset() {
        return pageOffset;
    }

    public Index setPrimaryKey(PrimaryKey primaryKey) {
        this.primaryKey = primaryKey;
        return this;
    }
}
