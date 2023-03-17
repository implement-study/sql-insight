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
 * @author gxz gongxuanzhang@foxmail.com
 **/
public class CompactNullValueFactory implements ByteBeanFactory<CompactNullValue> {


    @Override
    public CompactNullValue swap(CompactNullValue bean, byte[] bytes) {
        ConstantSize.COMPACT_NULL.checkSize(bytes);
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        bean.value = buffer.getShort();
        return bean;
    }

    @Override
    public CompactNullValue create() {
        return null;
    }
}
