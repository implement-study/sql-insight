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
import org.gongxuanzhang.mysql.entity.ShowLength;

import java.util.List;

/**
 * 用户数据组合
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
@Data
public class UserRecords implements ByteSwappable<UserRecords>, ShowLength {

    byte[] source;

    List<UserRecord> userRecordList;


    @Override
    public int length() {
        return source.length;
    }

    @Override
    public byte[] toBytes() {
        return source;
    }

    @Override
    public UserRecords fromBytes(byte[] bytes) {
        //  todo
        return null;
    }
}
