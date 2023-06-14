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


import org.gongxuanzhang.mysql.constant.Constant;
import org.gongxuanzhang.mysql.entity.BeanSupplier;

import java.nio.ByteBuffer;


/**
 * File Header 工厂 {@link FileHeader}
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
public class FileHeaderFactory implements ByteBeanSwapper<FileHeader>, BeanSupplier<FileHeader> {

    public static final int FILE_HEADER_INIT_CHECKSUM = Constant.INIT_CHECKSUM;


    @Override
    public FileHeader swap(byte[] bytes) {
        FileHeader bean = new FileHeader();
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        bean.checkSum = buffer.getInt();
        bean.offset = buffer.getInt();
        bean.pageType = buffer.getShort();
        bean.pre = buffer.getInt();
        bean.next = buffer.getInt();
        bean.lsn = buffer.getLong();
        bean.flushLsn = buffer.getLong();
        bean.spaceId = buffer.getInt();
        return bean;
    }


    @Override
    public FileHeader create() {
        FileHeader fileHeader = new FileHeader();
        fileHeader.setCheckSum(FILE_HEADER_INIT_CHECKSUM);
        fileHeader.setPageType(PageType.FIL_PAGE_TYPE_ALLOC.getValue());
        fileHeader.setPre(0);
        fileHeader.setNext(0);
        //  unused
        fileHeader.setLsn(0L);
        fileHeader.setSpaceId(0);
        fileHeader.setLsn(0L);
        fileHeader.setFlushLsn(0L);
        return fileHeader;
    }

}
