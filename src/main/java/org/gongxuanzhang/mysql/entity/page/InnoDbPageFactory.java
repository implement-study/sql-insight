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
import java.util.ArrayList;
import java.util.List;

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
public class InnoDbPageFactory implements ByteBeanFactory<InnoDbPage> {


    @Override
    public InnoDbPage swap(InnoDbPage bean, byte[] bytes) {
        ConstantSize.PAGE.checkSize(bytes);
        ByteBuffer buffer = ByteBuffer.wrap(bytes);

        //  file header
        byte[] candidate = new byte[FILE_HEADER.getSize()];
        buffer.get(candidate);
        FileHeaderFactory fileHeaderFactory = new FileHeaderFactory();
        fileHeaderFactory.swap(bean.getFileHeader(), candidate);

        //  page header
        candidate = new byte[PAGE_HEADER.getSize()];
        buffer.get(candidate);
        PageHeaderFactory pageHeaderFactory = new PageHeaderFactory();
        pageHeaderFactory.swap(bean.getPageHeader(), candidate);

        //  infimum
        candidate = new byte[INFIMUM.getSize()];
        buffer.get(candidate);
        InfimumFactory infimumFactory = new InfimumFactory();
        infimumFactory.swap(bean.getInfimum(), candidate);

        //  supremum
        candidate = new byte[SUPREMUM.getSize()];
        buffer.get(candidate);
        SupremumFactory supremumFactory = new SupremumFactory();
        supremumFactory.swap(bean.getSupremum(), candidate);

        //  user records 使用
        short heapTop = bean.pageHeader.getHeapTop();
        int userRecordLength = heapTop - buffer.position();
        candidate = new byte[userRecordLength];
        buffer.get(candidate);
        UserRecordsFactory userRecordsFactory = new UserRecordsFactory();
        userRecordsFactory.swap(bean.getUserRecords(), candidate);

        //  此时需要从后面往前读，因为不知道空闲空间有多少
        //  file trailer
        int freeMark = buffer.position();

        buffer.position(buffer.capacity() - candidate.length);
        candidate = new byte[ConstantSize.FILE_TRAILER.getSize()];
        buffer.get(candidate);
        FileTrailerFactory trailerFactory = new FileTrailerFactory();
        trailerFactory.swap(bean.getFileTrailer(), candidate);
        // page directory
        //   减2是因为slot里面是short  一个short 两个字节
        int shortLength = 2;
        buffer.position(buffer.capacity() - candidate.length - shortLength);
        int position = buffer.position();
        List<Short> slots = new ArrayList<>();
        while (true) {
            short slot = buffer.getShort();
            slots.add(slot);
            //  当槽位指向上确界的时候跳出循环 上确界的位置是 file header + page header 剩下的都是空闲空间
            if (slot == FILE_HEADER.getSize() + PAGE_HEADER.getSize()) {
                break;
            }
            position = position - shortLength;
            buffer.position(position);
        }
        short[] finalSlots = new short[slots.size()];
        for (int i = 0; i < slots.size(); i++) {
            finalSlots[i] = slots.get(i);
        }
        bean.pageDirectory.setSlots(finalSlots);
        bean.freeSpace = (short) (position - freeMark);
        return bean;
    }


    /**
     * 创建新页
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
        innoDbPage.setFreeSpace(initFreeSpaceLength(innoDbPage));
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
