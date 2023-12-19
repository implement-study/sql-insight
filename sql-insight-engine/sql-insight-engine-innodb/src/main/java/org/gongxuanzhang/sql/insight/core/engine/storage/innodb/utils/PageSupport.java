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
import org.gongxuanzhang.sql.insight.core.environment.SessionContext;
import org.gongxuanzhang.sql.insight.core.exception.RuntimeIoException;
import org.gongxuanzhang.sql.insight.core.object.Column;
import org.gongxuanzhang.sql.insight.core.object.Table;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
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
    private static void fillNullAndVar(InnoDbPage page, int offset, Compact compact) {
        SessionContext currentSession = SessionContext.getCurrentSession();
        Table table = currentSession.getTable();
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
        int nullIndex = 0;
        int result = 0;
        for (Column column : columnList) {
            if (!column.isNotNull() && nullList.isNull(nullIndex++)) {
                continue;
            }
            if (column.isVariable()) {
                result++;
            }
        }
        return result;
    }


    public static Compact readCompact(InnoDbPage page, int offset) {
        Compact compact = new Compact();
        compact.setRecordHeader(readRecordHeader(page, offset));
        fillNullAndVar(page, offset, compact);
        int variableLength = compact.getVariables().variableLength();
        byte[] body = new byte[variableLength];
        compact.setBody(body);
        return compact;
    }


}
