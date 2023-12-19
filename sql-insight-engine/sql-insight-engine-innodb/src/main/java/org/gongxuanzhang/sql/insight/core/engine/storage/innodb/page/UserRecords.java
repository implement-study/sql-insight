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

import org.gongxuanzhang.easybyte.core.ByteWrapper;
import org.gongxuanzhang.easybyte.core.DynamicByteBuffer;
import org.gongxuanzhang.sql.insight.core.object.UserRecord;

import java.util.ArrayList;
import java.util.List;

/**
 * @author gongxuanzhangmelt@gmail.com
 **/
public class UserRecords implements ByteWrapper {


    private final List<UserRecord> records = new ArrayList<>();


    public void addRecords(UserRecord userRecord) {
        this.records.add(userRecord);
    }

    @Override
    public byte[] toBytes() {
        DynamicByteBuffer buffer = DynamicByteBuffer.allocate();
        for (UserRecord record : records) {
            buffer.append(record.toBytes());
        }
        return buffer.toBytes();
    }
}