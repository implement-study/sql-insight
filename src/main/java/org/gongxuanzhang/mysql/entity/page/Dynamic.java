/*
 * Copyright 2023 sql-insight  and the original author or authors <gongxuanzhangmelt@gmail.com>.
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

package org.gongxuanzhang.mysql.entity.page;

import lombok.Data;
import org.gongxuanzhang.mysql.core.ByteSwappable;

/**
 * dynamic行格式
 *
 * @author gxz gongxuanzhangmelt@gmail.com
 **/
@Data
public class Dynamic implements UserRecord, ByteSwappable {


    @Override
    public byte[] toBytes() {
        //  todo
        return new byte[0];
    }

    @Override
    public RecordHeader getRecordHeader() {
        return null;
    }


    @Override
    public int length() {
        return 0;
    }
}
