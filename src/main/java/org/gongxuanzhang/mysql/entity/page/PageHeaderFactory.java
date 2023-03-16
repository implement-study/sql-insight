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

import java.nio.ByteBuffer;

/**
 * PageHeader 工厂 {@link PageHeader}
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
public class PageHeaderFactory implements ByteBeanFactory<PageHeader> {

    @Override
    public PageHeader swap(PageHeader bean, byte[] bytes) {
        ConstantSize.PAGE_HEADER_SIZE.checkSize(bytes);
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        bean.slotCount = buffer.getShort();
        bean.heapTop = buffer.getShort();
        bean.absoluteRecordCount = buffer.getShort();
        bean.recordCount = buffer.getShort();
        bean.free = buffer.getShort();
        bean.garbage = buffer.getShort();
        bean.lastInsert = buffer.getShort();
        bean.direction = buffer.getShort();
        bean.directionCount = buffer.getShort();
        bean.maxTransactionId = buffer.getLong();
        bean.level = buffer.getShort();
        bean.indexId = buffer.getLong();
        bean.segLeaf = buffer.getLong();
        bean.segTop = buffer.getLong();
        return bean;
    }

    @Override
    public PageHeader create() {
        PageHeader pageHeader = new PageHeader();
        //  todo
//        pageHeader.setSlotCount(0);
//        pageHeader.setHeapTop();
//        pageHeader.setAbsoluteRecordCount();
//        pageHeader.setRecordCount();
//        pageHeader.setFree();
//        pageHeader.setGarbage();
//        pageHeader.setLastInsert();
//        pageHeader.setLevel();
        pageHeader.setDirection((short) 0);
        pageHeader.setDirectionCount((short) 0);
        pageHeader.setMaxTransactionId(0L);
        pageHeader.setIndexId(0L);
        pageHeader.setSegLeaf(0L);
        pageHeader.setSegTop(0L);
        return pageHeader;
    }


}
