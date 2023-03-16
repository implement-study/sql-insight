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
import java.nio.charset.StandardCharsets;


/**
 * Infimum 工厂 {@link Infimum}
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
public class InfimumFactory implements ByteBeanFactory<Infimum> {

    @Override
    public Infimum swap(Infimum bean, byte[] bytes) {
        ConstantSize.INFIMUM_SIZE.checkSize(bytes);
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        byte[] headBuffer = new byte[ConstantSize.RECORD_HEADER.getSize()];
        buffer.get(headBuffer);
        bean.recordHeader = new RecordHeader(headBuffer);
        bean.body = new byte[ConstantSize.INFIMUM_BODY_SIZE.getSize()];
        buffer.get(bean.body);
        return bean;
    }

    @Override
    public Infimum create() {
        Infimum infimum = new Infimum();
        infimum.body = infimumInitBody();
        infimum.recordHeader = new RecordHeaderFactory().createInfimumHeader();
        return infimum;
    }


    private byte[] infimumInitBody() {
        ByteBuffer bodyBuffer = ByteBuffer.allocate(ConstantSize.INFIMUM_BODY_SIZE.getSize());
        bodyBuffer.put(Infimum.INFIMUM_BODY.getBytes(StandardCharsets.UTF_8));
        return bodyBuffer.array();
    }

}
