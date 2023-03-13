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

/**
 * 页目录
 * 里面有N个slot
 * 每个slot中只记录一个偏移量
 *
 * @author gxz gongxuanzhangmelt@gmail.com
 **/
@Data
public class PageDirectory implements ByteSwappable<PageDirectory> {

    /**
     * slot的每个数字记录每个槽中最大数据的偏移位置
     **/
    int[] slots;


    @Override
    public byte[] toBytes() {
        //  todo
        return new byte[0];
    }

    @Override
    public PageDirectory fromBytes(byte[] bytes) {
        //  todo
        return null;
    }
}
