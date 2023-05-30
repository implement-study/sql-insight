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

package org.gongxuanzhang.mysql.tool;

import org.gongxuanzhang.mysql.annotation.DependOnContext;
import org.gongxuanzhang.mysql.entity.TableInfo;
import org.gongxuanzhang.mysql.entity.page.FileHeader;
import org.gongxuanzhang.mysql.entity.page.InnoDbPage;
import org.gongxuanzhang.mysql.exception.MySQLException;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * @author gxz gongxuanzhang@foxmail.com
 **/
public class PageWriter {


    @DependOnContext
    public static void write(InnoDbPage page) throws MySQLException {
        FileHeader fileHeader = page.getFileHeader();
        int spaceId = fileHeader.getSpaceId();
        TableInfo tableInfo = Context.getTableManager().select(spaceId);
        try (RandomAccessFile randomAccessFile = new RandomAccessFile(tableInfo.dataFile(), "rw")) {
            randomAccessFile.skipBytes(fileHeader.getOffset());
            randomAccessFile.write(page.toBytes());
        } catch (IOException e) {
            ExceptionThrower.errorSwap(e);
        }
    }

}
