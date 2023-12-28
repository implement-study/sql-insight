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

package org.gongxuanzhang.sql.insight.core.engine.storage.innodb.page;

import lombok.Data;
import org.gongxuanzhang.easybyte.core.ByteWrapper;
import org.gongxuanzhang.easybyte.core.DynamicByteBuffer;


/**
 * 56 bytes.
 * record page info that may change frequently
 *
 * @author gxz gongxuanzhangmelt@gmail.com
 **/
@Data
public class PageHeader implements PageObject, ByteWrapper {

    /**
     * page slot count
     **/
    short slotCount;

    /**
     * offset of free space start
     **/
    short heapTop;

    /**
     * page record count include infimum and supremum and deleted record
     **/
    short absoluteRecordCount;

    /**
     * page record count exclude infimum and supremum and deleted record
     **/
    short recordCount;

    /**
     * the first deleted record in page. use next_record field can find delete linked list, init is 0
     **/
    short free;

    /**
     * deleted record occupy space
     **/
    short garbage;

    /**
     * last insert record offset
     **/
    short lastInsertOffset;
    /**
     * insert direction that use for support insert.
     * 0 is left.
     * 1 is right.
     **/
    short direction;
    /**
     * number of inserts in the same direction
     **/
    short directionCount;

    /**
     * the max transaction id in page
     **/
    long maxTransactionId;

    /**
     * this page in b-tree layer level
     * leaf node level is 0.
     **/
    short level;

    /**
     * which index the page belong to
     **/
    int indexId;
    /**
     * 10 bytes.
     * b-tree leaf-node header info . only root page have.
     **/
    long segLeaf;
    /**
     * 10 bytes.
     * b-tree non-leaf-node header info . only root page have.
     **/
    long segTop;


    @Override
    public int length() {
        return ConstantSize.PAGE_HEADER.size();
    }

    @Override
    public byte[] toBytes() {
        DynamicByteBuffer buffer = DynamicByteBuffer.allocate();
        buffer.appendShort(slotCount);
        buffer.appendShort(heapTop);
        buffer.appendShort(absoluteRecordCount);
        buffer.appendShort(recordCount);
        buffer.appendShort(free);
        buffer.appendShort(garbage);
        buffer.appendShort(lastInsertOffset);
        buffer.appendShort(direction);
        buffer.appendShort(directionCount);
        buffer.appendLong(maxTransactionId);
        buffer.appendShort(level);
        buffer.appendLong(indexId);
        buffer.appendLong(segLeaf);
        buffer.appendLong(segTop);
        return buffer.toBytes();
    }
}
