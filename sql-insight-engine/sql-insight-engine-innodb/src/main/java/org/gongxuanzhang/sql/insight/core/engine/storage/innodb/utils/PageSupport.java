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
import org.gongxuanzhang.sql.insight.core.engine.storage.innodb.page.ConstantSize;
import org.gongxuanzhang.sql.insight.core.engine.storage.innodb.page.InnoDbPage;
import org.gongxuanzhang.sql.insight.core.engine.storage.innodb.page.InnodbUserRecord;
import org.gongxuanzhang.sql.insight.core.engine.storage.innodb.page.RootPage;
import org.gongxuanzhang.sql.insight.core.engine.storage.innodb.page.Supremum;
import org.gongxuanzhang.sql.insight.core.engine.storage.innodb.page.compact.RowFormatFactory;
import org.gongxuanzhang.sql.insight.core.exception.RuntimeIoException;
import org.gongxuanzhang.sql.insight.core.object.Index;
import org.gongxuanzhang.sql.insight.core.object.UserRecord;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * @author gongxuanzhangmelt@gmail.com
 **/
@Slf4j
public class PageSupport {

    public static RootPage getRoot(Index index) {
        File indexFile = index.getFile();
        try (FileInputStream fileInputStream = new FileInputStream(indexFile)) {
            byte[] pageByte = ConstantSize.PAGE.emptyBuff();
            if (fileInputStream.read(pageByte) != pageByte.length) {
                throw new IllegalArgumentException("idb file error [ " + indexFile.getAbsoluteFile() + " ]");
            }
            return (RootPage) PageFactory.swap(pageByte, index);
        } catch (IOException e) {
            throw new RuntimeIoException(e);
        }
    }

    @Deprecated
    public static InnodbUserRecord getNextUserRecord(InnoDbPage page, UserRecord userRecord) {
        if (userRecord instanceof Supremum) {
            throw new NullPointerException("supremum is max record in page");
        }
        int nextRecordOffset = userRecord.nextRecordOffset();
        return RowFormatFactory.readRecordInPage(page, nextRecordOffset + userRecord.offset(), userRecord.belongTo());
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
