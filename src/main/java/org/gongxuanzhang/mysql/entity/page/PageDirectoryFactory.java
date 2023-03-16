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

import java.nio.ByteBuffer;

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

    @Override
    public PageDirectory create() {
        return null;
    }

}
