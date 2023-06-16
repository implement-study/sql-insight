package org.gongxuanzhang.mysql.entity.page;

import org.gongxuanzhang.mysql.constant.ConstantSize;

import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * 索引页的数据内容
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
public class IndexRecordFactory implements ByteBeanSwapper<Index> {


    /**
     * 通过页偏移量拿到index
     * 页必须是索引页
     *
     * @param page   innodb page
     * @param offset 偏移量
     * @return 返回index行格式
     **/
    public Index swap(InnoDbPage page, int offset) {
        byte[] source = page.getUserRecords().getSource();
        byte[] bytes = Arrays.copyOfRange(source, offset, source.length);
        return swap(bytes);
    }

    /**
     * 拿到下一个page
     *
     * @param page  innodb page
     * @param index 基础index
     * @return 返回下一个
     **/
    public Index nextIndex(InnoDbPage page, Index index) {
        int nextRecordOffset = index.getRecordHeader().getNextRecordOffset();
        return swap(page, nextRecordOffset);
    }

    @Override
    public Index swap(byte[] bytes) {
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        byte[] headerBuffer = ConstantSize.RECORD_HEADER.emptyBuff();
        buffer.get(headerBuffer);
        short length = buffer.getShort();
        byte[] bodyBuffer = new byte[length];
        buffer.get(bodyBuffer);
        RecordHeader swap = new RecordHeaderFactory().swap(headerBuffer);
        Index index = new Index();
        index.setRecordHeader(swap);
        index.setIndexLength(length);
        index.setIndexBody(bodyBuffer);
        return index;
    }
}
