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

/**
 * RecordHeader 工厂 {@link RecordHeader}
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
public class RecordHeaderFactory implements ByteBeanFactory<RecordHeader> {

    @Override
    public RecordHeader swap(RecordHeader bean, byte[] bytes) {
        ConstantSize.RECORD_HEADER.checkSize(bytes);
        bean.source.put(bytes);
        return bean;
    }

    @Override
    public RecordHeader create() {
        throw new UnsupportedOperationException("use createInfimumHeader() or createSupremumHeader()");
    }


    /**
     * 创建下确界记录头
     **/
    public RecordHeader createInfimumHeader() {
        return new RecordHeader(new byte[0]);
    }

    /**
     * 创建上确界记录头
     **/
    public RecordHeader createSupremumHeader() {
        return new RecordHeader(new byte[0]);
    }


}
