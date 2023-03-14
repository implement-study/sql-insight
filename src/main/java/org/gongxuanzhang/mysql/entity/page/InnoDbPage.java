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


import lombok.Data;
import org.gongxuanzhang.mysql.core.ByteSwappable;
import org.gongxuanzhang.mysql.core.factory.ConstantSize;
import org.gongxuanzhang.mysql.core.factory.InnoDbPageFactory;
import org.gongxuanzhang.mysql.entity.ShowLength;

import java.nio.ByteBuffer;

/**
 * InnoDb 页结构
 * 默认16K 暂时不支持修改
 *
 * @author gxz gongxuanzhangmelt@gmail.com
 **/
@Data
public class InnoDbPage implements ByteSwappable<InnoDbPage>, ShowLength {


    /**
     * 文件头 38字节
     **/
    private FileHeader fileHeader;
    /**
     * 页头 56字节
     **/
    private PageHeader pageHeader;
    /**
     * 下确界，13字节
     **/
    private Infimum infimum;
    /**
     * 上确界，13字节
     **/
    private Supremum supremum;
    /**
     * 用户记录  不确定字节
     **/
    private UserRecords userRecords;
    /**
     * 空闲空间，这里只记录字节数
     **/
    private int freeSpace;
    /**
     * 页目录
     **/
    private PageDirectory pageDirectory;
    /**
     * 文件尾 8字节
     **/
    private FileTrailer fileTrailer;


    public InnoDbPage() {
        this.fileHeader = new FileHeader();
        this.pageHeader = new PageHeader();
        this.infimum = new Infimum();
        this.supremum = new Supremum();
        this.userRecords = new UserRecords();
        this.pageDirectory = new PageDirectory();
        this.fileTrailer = new FileTrailer();
        this.freeSpace = ConstantSize.PAGE_SIZE.getSize() - fixLength();

    }

    /**
     * 返回固定字节数
     **/
    private int fixLength() {
        return this.fileHeader.length() +
                this.pageHeader.length() +
                this.infimum.length() +
                this.supremum.length() +
                this.userRecords.length() +
                this.pageDirectory.length() +
                this.fileTrailer.length();
    }


    public byte[] toBytes() {
        ByteBuffer buffer = ByteBuffer.allocate(length());
        buffer.put(fileHeader.toBytes());
        buffer.put(pageHeader.toBytes());
        buffer.put(infimum.toBytes());
        buffer.put(supremum.toBytes());
        buffer.put(userRecords.toBytes());
        buffer.position(buffer.position() + freeSpace);
        buffer.put(pageDirectory.toBytes());
        buffer.put(fileTrailer.toBytes());
        return buffer.array();
    }

    @Override
    public InnoDbPage fromBytes(byte[] bytes) {
        return new InnoDbPageFactory().swap(bytes);
    }


    @Override
    public int length() {
        return ConstantSize.PAGE_SIZE.getSize();
    }
}
