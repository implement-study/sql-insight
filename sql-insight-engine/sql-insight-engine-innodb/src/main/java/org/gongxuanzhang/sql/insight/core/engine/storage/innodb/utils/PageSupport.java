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

package org.gongxuanzhang.sql.insight.core.engine.storage.innodb.utils;

import lombok.extern.slf4j.Slf4j;
import org.gongxuanzhang.sql.insight.core.engine.storage.innodb.factory.PageFactory;
import tech.insight.engine.innodb.index.InnodbIndex;
import tech.insight.engine.innodb.page.ConstantSize;
import tech.insight.engine.innodb.page.InnoDbPage;
import org.gongxuanzhang.sql.insight.core.exception.RuntimeIoException;
import org.gongxuanzhang.sql.insight.core.object.Index;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * @author gongxuanzhangmelt@gmail.com
 **/
@Slf4j
public class PageSupport {

    public static InnoDbPage getRoot(InnodbIndex index) {
        File indexFile = index.getFile();
        try (FileInputStream fileInputStream = new FileInputStream(indexFile)) {
            byte[] pageByte = ConstantSize.PAGE.emptyBuff();
            if (fileInputStream.read(pageByte) != pageByte.length) {
                throw new IllegalArgumentException("idb file error [ " + indexFile.getAbsoluteFile() + " ]");
            }
            return PageFactory.swap(pageByte, index);
        } catch (IOException e) {
            throw new RuntimeIoException(e);
        }
    }


    /**
     * extension index file page count * page size.
     *
     * @return offset namely length of file before extension.
     **/
    public static int allocatePage(InnodbIndex index, int pageCount) {
        try (RandomAccessFile randomAccessFile = new RandomAccessFile(index.getFile(), "rw")) {
            long currentLength = randomAccessFile.length();
            randomAccessFile.setLength(currentLength + (long) ConstantSize.PAGE_HEADER.size() * pageCount);
            return (int) currentLength;
        } catch (IOException e) {
            throw new RuntimeIoException(e);
        }
    }

    public static int allocatePage(InnodbIndex index) {
        return allocatePage(index, 1);
    }


    public static void flushPage(InnoDbPage page) {
        Index belongIndex = page.getExt().getBelongIndex();
        File indexFile = belongIndex.getFile();
        try (RandomAccessFile randomAccessFile = new RandomAccessFile(indexFile, "rw")) {
            long minLength = page.getFileHeader().getOffset() + ConstantSize.PAGE.size();
            if (randomAccessFile.length() < minLength) {
                randomAccessFile.setLength(minLength);
            }
            randomAccessFile.seek(page.getFileHeader().getOffset());
            randomAccessFile.write(page.toBytes());
            log.info("write page to {}", indexFile.getAbsoluteFile());
        } catch (IOException e) {
            throw new RuntimeIoException(e);
        }
    }

    public static void flushPages(InnoDbPage... pages) {
        for (InnoDbPage page : pages) {
            flushPage(page);
        }
    }

}
