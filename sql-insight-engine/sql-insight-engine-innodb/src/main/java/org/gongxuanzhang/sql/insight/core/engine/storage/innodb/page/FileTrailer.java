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

package org.gongxuanzhang.sql.insight.core.engine.storage.innodb.page;

import lombok.Data;
import org.gongxuanzhang.easybyte.core.ByteWrapper;

import java.nio.ByteBuffer;


/**
 * use it with {@link FileHeader}
 * only in order to check.
 * @author gxz gongxuanzhangmelt@gmail.com
 **/
@Data
public class FileTrailer implements ByteWrapper, PageObject {

    /**
     * use it with {@link FileHeader#checkSum}
     */
    int checkSum;

    /**
     * use it with {@link FileHeader#lsn}
     **/
    int lsn;


    @Override
    public int length() {
        return ConstantSize.FILE_TRAILER.size();
    }

    @Override
    public byte[] toBytes() {
        ByteBuffer buffer = ByteBuffer.allocate(this.length());
        buffer.putInt(this.checkSum);
        buffer.putInt(this.lsn);
        return buffer.array();
    }
}