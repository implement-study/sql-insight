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

import static org.gongxuanzhang.mysql.constant.ConstantSize.FILE_HEADER;
import static org.gongxuanzhang.mysql.constant.ConstantSize.PAGE_HEADER;

/**
 * PageDirectory 工厂
 * {@link PageDirectory}
 *
 * @author gxz gongxuanzhangmelt@gmail.com
 **/
public class PageDirectoryFactory implements ByteBeanFactory<PageDirectory> {


    @Override
    public PageDirectory swap(PageDirectory bean, byte[] bytes) {
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        short[] slots = new short[bytes.length / 2];
        for (int i = bytes.length / 2; i > 0; i--) {
            slots[i] = buffer.getShort();
        }
        bean.slots = slots;
        return bean;
    }


    /**
     * 初始化页目录有两个槽
     * 一个是最小记录 一个是最大记录
     * 偏移量
     *
     * @return 返回个啥
     **/
    @Override
    public PageDirectory create() {
        PageDirectory pageDirectory = new PageDirectory();
        short[] slots = new short[2];
        //  下确界偏移量是  page header + file header
        short infimumOffset = (short) (PAGE_HEADER.getSize() + FILE_HEADER.getSize());
        //  上确捷是 下确界offset+下确界size
        short supremumOffset = (short) (infimumOffset + ConstantSize.INFIMUM.getSize());
        slots[0] = infimumOffset;
        slots[1] = supremumOffset;
        pageDirectory.setSlots(slots);
        return pageDirectory;
    }

}
