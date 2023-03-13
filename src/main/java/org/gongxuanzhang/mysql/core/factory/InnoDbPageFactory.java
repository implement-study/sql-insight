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
        ConstantSize.PAGE_SIZE.checkSize(bytes);
        ByteBuffer wrap = ByteBuffer.wrap(bytes);
        InnoDbPage page = new InnoDbPage();

        byte[] buffer = new byte[ConstantSize.FILE_HEADER_SIZE.getSize()];
        wrap.get(buffer);
        page.setFileHeader(createFileHeader(buffer));

        buffer = new byte[ConstantSize.PAGE_HEADER_SIZE.getSize()];
        wrap.get(buffer);
        page.setPageHeader(createPageHeader(buffer));

        buffer = new byte[ConstantSize.INFIMUM_SIZE.getSize()];
        wrap.get(buffer);
        page.setInfimum(createInfimum(buffer));

        buffer = new byte[ConstantSize.SUPREMUM_SIZE.getSize()];
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
        ConstantSize.FILE_HEADER_SIZE.checkSize(bytes);
        FileHeader fileHeader = new FileHeader();
        fileHeader.setCheckSum(INIT_CHECKSUM);
        return fileHeader;
    }

    public static PageHeader createPageHeader(byte[] bytes) {
        ConstantSize.PAGE_HEADER_SIZE.checkSize(bytes);
        PageHeader pageHeader = new PageHeader();
        return pageHeader;
    }

    public static Infimum createInfimum(byte[] bytes) {
        ConstantSize.INFIMUM_SIZE.checkSize(bytes);
        Infimum infimum = new Infimum();
        return infimum;
    }

    public static Supremum createSupremum(byte[] bytes) {
        ConstantSize.SUPREMUM_SIZE.checkSize(bytes);
        Supremum supremum = new Supremum();
        return supremum;
    }

    public static FileTrailer createFileTrailer() {
        FileTrailer fileTrailer = new FileTrailer();
        fileTrailer.setCheckSum(INIT_CHECKSUM);
        return fileTrailer;
    }


}
