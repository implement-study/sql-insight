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

package org.gongxuanzhang.mysql.tool;

import org.gongxuanzhang.mysql.constant.ConstantSize;
import org.gongxuanzhang.mysql.entity.Column;
import org.gongxuanzhang.mysql.entity.TableInfo;
import org.gongxuanzhang.mysql.entity.page.*;
import org.gongxuanzhang.mysql.exception.MySQLException;

import java.nio.ByteBuffer;

/**
 * 页相关工具类
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
public class PageUtils {

    private PageUtils() {

    }


    /**
     * 从数据页转换成索引页
     * <p>
     * 调用此方法一定是从数据页 转换成索引页
     *
     * @param candidatePage 等待被转换的页
     * @return 新的数据页
     **/
    public static InnoDbPage rootDataPageToIndex(InnoDbPage candidatePage) throws MySQLException {
        InnoDbPageFactory factory = InnoDbPageFactory.getInstance();
        InnoDbPage newDataPage = factory.copyPage(candidatePage);
        InnoDbPage newIndexPage = factory.create();
        //  连接
        newIndexPage.getFileHeader().setNext(ConstantSize.PAGE.getSize());
        newDataPage.getFileHeader().setOffset(ConstantSize.PAGE.getSize());
        //  调整状态
        newIndexPage.getFileHeader().setPageType(PageType.FIL_PAGE_INODE.getValue());


        UserRecord minUserData = PageUtils.getNextUserRecord(newDataPage, newDataPage.getInfimum());
        Index indexRow = new Index();
        RecordHeader indexHeader = new RecordHeader();
        indexHeader.setRecordType(RecordType.PAGE);
        indexHeader.setNextRecordOffset(ConstantSize.SUPREMUM.offset());
        indexHeader.setHeapNo(2);
        indexHeader.setMinRec(true);
        newIndexPage.getSupremum().getRecordHeader().setnOwned(2);
        indexRow.setRecordHeader(indexHeader);

        return newDataPage;

    }


    /**
     * 根据偏移量拿到页中的用户记录
     *
     * @param page   innodb page
     * @param offset 偏移量
     * @return 拿到用户记录
     **/
    public static UserRecord getUserRecordByOffset(InnoDbPage page, short offset) {
        if (offset == ConstantSize.INFIMUM.offset()) {
            return page.getInfimum();
        }
        if (offset == ConstantSize.SUPREMUM.offset()) {
            return page.getSupremum();
        }
        TableInfo tableInfo = page.getTableInfo();
        int bodyOffset = offset - ConstantSize.SUPREMUM.offset() - ConstantSize.SUPREMUM.getSize();
        byte[] bodySource = page.getUserRecords().getSource();
        ByteBuffer wrap = ByteBuffer.wrap(bodySource);
        wrap.position(bodyOffset);
        byte[] recordBuffer = ConstantSize.RECORD_HEADER.emptyBuff();
        wrap.get(recordBuffer);
        RecordHeader recordHeader = new RecordHeaderFactory().swap(recordBuffer);
        byte[] variablesBuffer = new byte[tableInfo.getVariableCount()];
        wrap.get(variablesBuffer);
        Variables variables = new Variables(variablesBuffer);
        CompactNullValue compactNullValue = new CompactNullValue(wrap.getShort());
        Compact compact = new Compact();
        long rowId = BitUtils.readLong(wrap, 6);
        long transactionId = BitUtils.readLong(wrap, 6);
        long rollPointer = BitUtils.readLong(wrap, 7);
        int bodyLength = bodyLength(variables, compactNullValue, tableInfo);
        byte[] body = new byte[bodyLength];
        wrap.get(body);
        compact.setBody(body);
        compact.setVariables(variables);
        compact.setNullValues(compactNullValue);
        compact.setRecordHeader(recordHeader);
        compact.setRollPointer(rollPointer);
        compact.setRowId(rowId);
        compact.setTransactionId(transactionId);
        compact.setPageOffset(offset);
        return compact;
    }


    /**
     * 拿到body真正的长度
     * 一个列只有三种情况，是null，可变，固定
     * 所以把null和固定的加起来，然后把可边长总长度加起来就oK
     *
     * @param variables        可变长度
     * @param compactNullValue null值列表
     * @param tableInfo        表结构
     * @return body 长度
     **/
    public static int bodyLength(Variables variables, CompactNullValue compactNullValue, TableInfo tableInfo) {
        int bodyLength = 0;
        for (int i = 0; i < tableInfo.getColumns().size(); i++) {
            Column currentCol = tableInfo.getColumns().get(i);
            Integer nullIndex = currentCol.getNullIndex();
            if (nullIndex != null && compactNullValue.isNull(nullIndex)) {
                continue;
            }
            if (!currentCol.isDynamic()) {
                bodyLength += currentCol.getLength();
            }
        }
        bodyLength += variables.variableLength();
        return bodyLength;
    }

    /**
     * 拿到目标记录的下一个记录
     *
     * @param userRecord 目标记录
     * @return 返回记录的下一个
     **/
    public static UserRecord getNextUserRecord(InnoDbPage page, UserRecord userRecord) {
        if (userRecord instanceof Supremum) {
            throw new NullPointerException("supremum 没有下一个");
        }
        int nextRecordOffset = userRecord.getRecordHeader().getNextRecordOffset();
        return getUserRecordByOffset(page, (short) nextRecordOffset);
    }

}
