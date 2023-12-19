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

package org.gongxuanzhang.sql.insight.core.engine.storage.innodb.utils;

import org.gongxuanzhang.sql.insight.core.engine.storage.innodb.factory.PageFactory;
import org.gongxuanzhang.sql.insight.core.engine.storage.innodb.page.ConstantSize;
import org.gongxuanzhang.sql.insight.core.engine.storage.innodb.page.InnoDbPage;
import org.gongxuanzhang.sql.insight.core.engine.storage.innodb.page.RootPage;
import org.gongxuanzhang.sql.insight.core.engine.storage.innodb.page.compact.Compact;
import org.gongxuanzhang.sql.insight.core.engine.storage.innodb.page.compact.CompactNullList;
import org.gongxuanzhang.sql.insight.core.engine.storage.innodb.page.compact.RecordHeader;
import org.gongxuanzhang.sql.insight.core.engine.storage.innodb.page.compact.Variables;
import org.gongxuanzhang.sql.insight.core.exception.RuntimeIoException;
import org.gongxuanzhang.sql.insight.core.object.Column;
import org.gongxuanzhang.sql.insight.core.object.ReadRow;
import org.gongxuanzhang.sql.insight.core.object.Table;
import org.gongxuanzhang.sql.insight.core.object.value.Value;
import org.gongxuanzhang.sql.insight.core.object.value.ValueInt;
import org.gongxuanzhang.sql.insight.core.object.value.ValueNegotiator;
import org.gongxuanzhang.sql.insight.core.object.value.ValueNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author gongxuanzhangmelt@gmail.com
 **/
public class PageSupport {

    public static RootPage getRoot(File ibd) {
        try (FileInputStream fileInputStream = new FileInputStream(ibd)) {
            byte[] pageByte = ConstantSize.PAGE.emptyBuff();
            if (fileInputStream.read(pageByte) != pageByte.length) {
                throw new IllegalArgumentException("idb file error [ " + ibd.getAbsoluteFile() + " ]");
            }
            return (RootPage) PageFactory.swap(pageByte);
        } catch (IOException e) {
            throw new RuntimeIoException(e);
        }
    }


    /**
     * @param page   innodb page
     * @param offset record header offset
     * @return record
     **/
    public static RecordHeader readRecordHeader(InnoDbPage page, int offset) {
        int recordHeaderSize = ConstantSize.RECORD_HEADER.size();
        ByteBuffer buffer = ByteBuffer.wrap(page.getSource(), offset - recordHeaderSize, recordHeaderSize);
        return new RecordHeader(buffer.array());
    }


    /**
     * fill compact field null list and variables
     * depend on table info.
     **/
    private static void fillNullAndVar(InnoDbPage page, int offset, Compact compact, Table table) {
        int nullLength = table.getExt().getNullableColCount() / Byte.SIZE;
        ByteBuffer pageBuffer = ByteBuffer.wrap(page.getSource());
        byte[] nullListByte = new byte[nullLength];
        offset -= ConstantSize.RECORD_HEADER.size() - nullLength;
        pageBuffer.get(nullListByte, offset, nullLength);
        //   read null list
        CompactNullList compactNullList = new CompactNullList(nullListByte);
        compact.setNullList(compactNullList);
        //   read variable
        int variableCount = variableColumnCount(table, compactNullList);
        byte[] variableArray = new byte[variableCount];
        offset -= variableCount;
        pageBuffer.get(variableArray, offset, variableCount);
        compact.setVariables(new Variables(variableArray));
    }

    private static int variableColumnCount(Table table, CompactNullList nullList) {
        List<Column> columnList = table.getColumnList();
        int result = 0;
        for (Column column : columnList) {
            if (!column.isNotNull() && nullList.isNull(column.getNullListIndex())) {
                continue;
            }
            if (column.isVariable()) {
                result++;
            }
        }
        return result;
    }


    public static Compact readCompact(InnoDbPage page, int offset, Table table) {
        Compact compact = new Compact();
        compact.setRecordHeader(readRecordHeader(page, offset));
        fillNullAndVar(page, offset, compact, table);
        int variableLength = compact.getVariables().variableLength();
        int fixLength = compactFixLength(compact, table);
        byte[] body = new byte[variableLength + fixLength];
        compact.setBody(body);
        compact.setSourceRow(compactReadRow(compact, table));
        return compact;
    }

    private static int compactFixLength(Compact compact, Table table) {
        int fixLength = 0;
        for (Column column : table.getColumnList()) {
            if (column.isVariable()) {
                continue;
            }
            if (column.isNotNull() || (!compact.getNullList().isNull(column.getNullListIndex()))) {
                fixLength += column.getDataType().getLength();
            }
        }
        return fixLength;
    }

    private static ReadRow compactReadRow(Compact compact, Table table) {
        List<Column> columnList = table.getColumnList();
        List<Value> valueList = new ArrayList<>(columnList.size());
        ByteBuffer bodyBuffer = ByteBuffer.wrap(compact.getBody());
        Iterator<Byte> iterator = compact.getVariables().iterator();
        int rowId = -1;
        for (Column column : columnList) {
            if (!column.isNotNull() && compact.getNullList().isNull(column.getNullListIndex())) {
                valueList.add(column.getDefaultValue() == null ? ValueNull.getInstance() : column.getDefaultValue());
                continue;
            }
            int length = column.isVariable() ? iterator.next() : column.getDataType().getLength();
            byte[] item = new byte[length];
            bodyBuffer.get(item);
            Value value = ValueNegotiator.wrapValue(column, item);
            valueList.add(value);
            if (column.isPrimaryKey()) {
                rowId = ((ValueInt) value).getSource();
            }
        }
        ReadRow row = new ReadRow(valueList, rowId);
        row.setTable(table);
        return row;
    }


}
