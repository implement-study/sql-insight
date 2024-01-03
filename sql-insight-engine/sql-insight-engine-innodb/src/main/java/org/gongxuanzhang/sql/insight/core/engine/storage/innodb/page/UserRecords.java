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

package org.gongxuanzhang.sql.insight.core.engine.storage.innodb.page;

import lombok.EqualsAndHashCode;
import org.gongxuanzhang.easybyte.core.ByteWrapper;
import org.gongxuanzhang.easybyte.core.DynamicByteBuffer;

/**
 * @author gongxuanzhangmelt@gmail.com
 **/
@EqualsAndHashCode
public class UserRecords implements ByteWrapper, PageObject {


    byte[] body;

    public UserRecords() {
        this(new byte[0]);
    }

    public UserRecords(byte[] body) {
        this.body = body;
    }


    @Override
    public byte[] toBytes() {
        return this.body;
    }

    public void addRecord(InnodbUserRecord userRecord) {
        this.body = DynamicByteBuffer.wrap(this.body).append(userRecord.toBytes()).toBytes();
    }

    @Override
    public int length() {
        return body.length;
    }


}
