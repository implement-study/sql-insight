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
 * 页工厂
 * {@link InnoDbPage}
 *
 * @author gxz gongxuanzhangmelt@gmail.com
 **/
public class InnoDbPageFactory implements ByteBeanFactory<InnoDbPage> {


    @Override
    public InnoDbPage swap(InnoDbPage bean, byte[] bytes) {
        ConstantSize.PAGE_SIZE.checkSize(bytes);
        ByteBuffer wrap = ByteBuffer.wrap(bytes);

        byte[] buffer = new byte[ConstantSize.FILE_HEADER_SIZE.getSize()];
        wrap.get(buffer);
        FileHeaderFactory fileHeaderFactory = new FileHeaderFactory();
        fileHeaderFactory.swap(bean.getFileHeader(), buffer);

        buffer = new byte[ConstantSize.PAGE_HEADER_SIZE.getSize()];
        wrap.get(buffer);
        PageHeaderFactory pageHeaderFactory = new PageHeaderFactory();
        pageHeaderFactory.swap(bean.getPageHeader(), buffer);

        buffer = new byte[ConstantSize.INFIMUM_SIZE.getSize()];
        wrap.get(buffer);
        InfimumFactory infimumFactory = new InfimumFactory();
        infimumFactory.swap(bean.getInfimum(), buffer);

        buffer = new byte[ConstantSize.SUPREMUM_SIZE.getSize()];
        wrap.get(buffer);
        SupremumFactory supremumFactory = new SupremumFactory();
        supremumFactory.swap(bean.getSupremum(), buffer);

        // todo  读body,pageDir，file trailer

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
        //  todo
        innoDbPage.setUserRecords(new UserRecordsFactory().create());
        innoDbPage.setPageDirectory(new PageDirectoryFactory().create());
        innoDbPage.setFileTrailer(new FileTrailerFactory().create());
        innoDbPage.setFreeSpace(initFreeSpaceLength(innoDbPage));
        return innoDbPage;
    }

    private int initFreeSpaceLength(InnoDbPage innoDbPage) {
        return ConstantSize.PAGE_SIZE.getSize() -
                innoDbPage.getFileHeader().length() -
                innoDbPage.getPageHeader().length() -
                innoDbPage.getInfimum().length() -
                innoDbPage.getSupremum().length() -
                innoDbPage.getUserRecords().length() -
                innoDbPage.getPageDirectory().length() -
                innoDbPage.getFileTrailer().length();
    }


}
