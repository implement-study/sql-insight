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

package org.gongxuanzhang.mysql.tool;

import org.gongxuanzhang.mysql.constant.ConstantSize;
import org.gongxuanzhang.mysql.entity.page.InnoDbPage;
import org.gongxuanzhang.mysql.entity.page.InnoDbPageFactory;
import org.gongxuanzhang.mysql.exception.MySQLException;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * @author gxz gongxuanzhang@foxmail.com
 **/
public class PageReader {

    public static int read(File file, byte[] buffer, int skip) throws MySQLException {
        try (RandomAccessFile target = new RandomAccessFile(file, "r")) {
            if (skip == 0) {
                return target.read(buffer);
            }
            target.skipBytes(skip);
            return target.read(buffer);
        } catch (IOException e) {
            return ExceptionThrower.errorSwap(e);
        }
    }

    public static int read(File file, byte[] buffer) throws MySQLException {
        return read(file, buffer, 0);
    }

    public static InnoDbPage readInnodbPage(File file, int offset) throws MySQLException {
        byte[] pageBuffer = ConstantSize.PAGE.emptyBuff();
        try (RandomAccessFile target = new RandomAccessFile(file, "r")) {
            if (offset != 0) {
                target.skipBytes(offset);
            }
            int readLength = target.read(pageBuffer);
            if (readLength != pageBuffer.length) {
                throw new MySQLException("错误的偏移量：" + offset);
            }
            InnoDbPageFactory factory = InnoDbPageFactory.getInstance();
            return factory.swap(pageBuffer);
        } catch (IOException e) {
            return ExceptionThrower.errorSwap(e);
        }
    }


}
