/*
 * Copyright 2023 java-mysql  and the original author or authors <gongxuanzhangmelt@gmail.com>.
 *
 * Licensed under the GNU Affero General Public License v3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://github.com/implement-study/sql-insight/blob/main/LICENSE
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.gongxuanzhang.sql.insight.core.object.value;

import org.gongxuanzhang.easybyte.core.tool.ByteArrays;

/**
 * @author gongxuanzhangmelt@gmail.com
 **/
public class AutoIncrementValue implements Value {

    private Integer value;

    public AutoIncrementValue(Integer value) {
        this.value = value;
    }

    public AutoIncrementValue setValue(Integer value) {
        this.value = value;
        return this;
    }


    @Override
    public int getLength() {
        return Integer.BYTES;
    }

    @Override
    public Integer getSource() {
        return value;
    }

    @Override
    public byte[] toBytes() {
        return ByteArrays.fromInt(this.value);
    }
}