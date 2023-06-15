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
import org.gongxuanzhang.mysql.entity.BeanSupplier;

import java.nio.ByteBuffer;

import static org.gongxuanzhang.mysql.constant.ConstantSize.FILE_HEADER;
import static org.gongxuanzhang.mysql.constant.ConstantSize.INFIMUM;
import static org.gongxuanzhang.mysql.constant.ConstantSize.PAGE_HEADER;
import static org.gongxuanzhang.mysql.constant.ConstantSize.SUPREMUM;

/**
 * 页工厂
 * {@link InnoDbPage}
 *
 * @author gxz gongxuanzhangmelt@gmail.com
 **/
public class InnoDbPageFactory implements ByteBeanSwapper<InnoDbPage>, BeanSupplier<InnoDbPage> {

    public static final InnoDbPageFactory INSTANCE = new InnoDbPageFactory();

    private InnoDbPageFactory() {

    }

    public static InnoDbPageFactory getInstance() {
        return INSTANCE;
    }


    /**
     * 复制一个页
     *
     * @param source 复制的源
     * @return 返回复制的页
     **/
    public InnoDbPage copyPage(InnoDbPage source) {
        return swap(source.toBytes());
    }

    @Override
    public InnoDbPage swap(byte[] bytes) {
        InnoDbPage bean = new InnoDbPage();
        ConstantSize.PAGE.checkSize(bytes);
        ByteBuffer buffer = ByteBuffer.wrap(bytes);

        //  file header
        byte[] candidate = new byte[FILE_HEADER.getSize()];
        buffer.get(candidate);
        FileHeaderFactory fileHeaderFactory = new FileHeaderFactory();
        bean.setFileHeader(fileHeaderFactory.swap(candidate));

        //  page header
        candidate = new byte[PAGE_HEADER.getSize()];
        buffer.get(candidate);
        PageHeaderFactory pageHeaderFactory = new PageHeaderFactory();
        bean.setPageHeader(pageHeaderFactory.swap(candidate));

        //  infimum
        candidate = new byte[INFIMUM.getSize()];
        buffer.get(candidate);
        InfimumFactory infimumFactory = new InfimumFactory();
        bean.setInfimum(infimumFactory.swap(candidate));

        //  supremum
        candidate = new byte[SUPREMUM.getSize()];
        buffer.get(candidate);
        SupremumFactory supremumFactory = new SupremumFactory();
        bean.setSupremum(supremumFactory.swap(candidate));

        //  user records 使用
        short heapTop = bean.pageHeader.getHeapTop();
        int userRecordLength = heapTop - buffer.position();
        candidate = new byte[userRecordLength];
        buffer.get(candidate);
        UserRecordsFactory userRecordsFactory = new UserRecordsFactory();
        bean.setUserRecords(userRecordsFactory.swap(candidate));

        short slotCount = bean.pageHeader.slotCount;
        short slotByteLength = (short) (slotCount * 2);

        short freeLength = (short) (buffer.remaining() - slotByteLength - ConstantSize.FILE_TRAILER.getSize());
        buffer.position(buffer.position() + freeLength);
        bean.setFreeSpace(freeLength);
        candidate = new byte[slotByteLength];
        buffer.get(candidate);
        PageDirectory pd = new PageDirectoryFactory().swap(candidate);
        bean.setPageDirectory(pd);

        candidate = new byte[ConstantSize.FILE_TRAILER.getSize()];
        buffer.get(candidate);
        FileTrailerFactory trailerFactory = new FileTrailerFactory();
        FileTrailer fileTrailer = trailerFactory.swap(candidate);
        bean.setFileTrailer(fileTrailer);
        return bean;
    }


    /**
     * 创建一个新页
     **/
    @Override
    public InnoDbPage create() {
        InnoDbPage innoDbPage = new InnoDbPage();
        innoDbPage.setFileHeader(new FileHeaderFactory().create());
        innoDbPage.setPageHeader(new PageHeaderFactory().create());
        innoDbPage.setInfimum(new InfimumFactory().create());
        innoDbPage.setSupremum(new SupremumFactory().create());
        innoDbPage.setUserRecords(new UserRecordsFactory().create());
        innoDbPage.setPageDirectory(new PageDirectoryFactory().create());
        innoDbPage.setFileTrailer(new FileTrailerFactory().create());
        innoDbPage.setFreeSpace((short) ConstantSize.INIT_PAGE_FREE_SPACE.getSize());
        return innoDbPage;
    }

    private short initFreeSpaceLength(InnoDbPage innoDbPage) {
        return (short) (ConstantSize.PAGE.getSize() -
                innoDbPage.getFileHeader().length() -
                innoDbPage.getPageHeader().length() -
                innoDbPage.getInfimum().length() -
                innoDbPage.getSupremum().length() -
                innoDbPage.getUserRecords().length() -
                innoDbPage.getPageDirectory().length() -
                innoDbPage.getFileTrailer().length());
    }


}
