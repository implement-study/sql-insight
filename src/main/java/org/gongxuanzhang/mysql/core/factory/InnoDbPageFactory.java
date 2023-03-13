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

package org.gongxuanzhang.mysql.core.factory;

import org.gongxuanzhang.mysql.entity.page.FileHeader;
import org.gongxuanzhang.mysql.entity.page.FileTrailer;
import org.gongxuanzhang.mysql.entity.page.Infimum;
import org.gongxuanzhang.mysql.entity.page.InnoDbPage;
import org.gongxuanzhang.mysql.entity.page.PageDirectory;
import org.gongxuanzhang.mysql.entity.page.PageHeader;
import org.gongxuanzhang.mysql.entity.page.Supremum;

import java.nio.ByteBuffer;
import java.util.ArrayList;

/**
 * 页工厂
 *
 * @author gxz gongxuanzhangmelt@gmail.com
 **/
public class InnoDbPageFactory implements ByteBeanFactory<InnoDbPage> {


    public static final int INIT_CHECKSUM = 12345;


    @Override
    public InnoDbPage swap(byte[] bytes) {
        checkSize(bytes, ConstantSize.PAGE_SIZE);
        ByteBuffer wrap = ByteBuffer.wrap(bytes);
        InnoDbPage page = new InnoDbPage();

        byte[] buffer = new byte[ConstantSize.FILE_HEADER_SIZE.size];
        wrap.get(buffer);
        page.setFileHeader(createFileHeader(buffer));

        buffer = new byte[ConstantSize.PAGE_HEADER_SIZE.size];
        wrap.get(buffer);
        page.setPageHeader(createPageHeader(buffer));

        buffer = new byte[ConstantSize.INFIMUM_SIZE.size];
        wrap.get(buffer);
        page.setInfimum(createInfimum(buffer));

        buffer = new byte[ConstantSize.SUPREMUM_SIZE.size];
        wrap.get(buffer);
        page.setSupremum(createSupremum(buffer));

        page.setUserRecords(new ArrayList<>());
        page.setPageDirectory(createPageDirectory());

        page.setFileTrailer(createFileTrailer());

        return page;
    }

    @Override
    public InnoDbPage create() {
        InnoDbPage innoDbPage = new InnoDbPage();
        //  todo
        return innoDbPage;
    }


    public static PageDirectory createPageDirectory() {
        PageDirectory pageDirectory = new PageDirectory();
        pageDirectory.setSlots(new int[]{});
        return pageDirectory;
    }

    public static FileHeader createFileHeader(byte[] bytes) {
        checkSize(bytes, ConstantSize.FILE_HEADER_SIZE);
        FileHeader fileHeader = new FileHeader();
        fileHeader.setCheckSum(INIT_CHECKSUM);
        return fileHeader;
    }

    public static PageHeader createPageHeader(byte[] bytes) {
        checkSize(bytes, ConstantSize.PAGE_HEADER_SIZE);
        PageHeader pageHeader = new PageHeader();
        return pageHeader;
    }

    public static Infimum createInfimum(byte[] bytes) {
        checkSize(bytes, ConstantSize.INFIMUM_SIZE);
        Infimum infimum = new Infimum();
        return infimum;
    }

    public static Supremum createSupremum(byte[] bytes) {
        checkSize(bytes, ConstantSize.SUPREMUM_SIZE);
        Supremum supremum = new Supremum();
        return supremum;
    }

    public static FileTrailer createFileTrailer() {
        FileTrailer fileTrailer = new FileTrailer();
        fileTrailer.setCheckSum(INIT_CHECKSUM);
        return fileTrailer;
    }


    private static void checkSize(byte[] bytes, ConstantSize constantSize) {
        if (bytes.length != constantSize.size) {
            throw new IllegalArgumentException(constantSize.getDesc() + "大小必须是" + constantSize.size + "字节");
        }
    }


    public enum ConstantSize {
        PAGE_SIZE("页", 8 * 1024),
        FILE_HEADER_SIZE("文件头", 38),
        PAGE_HEADER_SIZE("页头", 56),
        INFIMUM_SIZE("下确界", 13),
        SUPREMUM_SIZE("上确界", 13);

        private final String desc;
        private final int size;

        ConstantSize(String desc, int size) {
            this.desc = desc;
            this.size = size;
        }

        public String getDesc() {
            return desc;
        }

        public int getSize() {
            return size;
        }
    }
}
