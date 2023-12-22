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

import org.gongxuanzhang.sql.insight.core.engine.storage.innodb.factory.PageFactory;
import org.gongxuanzhang.sql.insight.core.engine.storage.innodb.page.ConstantSize;
import org.gongxuanzhang.sql.insight.core.engine.storage.innodb.page.InnoDbPage;
import org.gongxuanzhang.sql.insight.core.engine.storage.innodb.page.InnodbUserRecord;
import org.gongxuanzhang.sql.insight.core.engine.storage.innodb.page.RootPage;
import org.gongxuanzhang.sql.insight.core.engine.storage.innodb.page.Supremum;
import org.gongxuanzhang.sql.insight.core.engine.storage.innodb.page.compact.RowFormatFactory;
import org.gongxuanzhang.sql.insight.core.exception.RuntimeIoException;
import org.gongxuanzhang.sql.insight.core.object.Table;
import org.gongxuanzhang.sql.insight.core.object.UserRecord;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * @author gongxuanzhangmelt@gmail.com
 **/
public class PageSupport {

    public static RootPage getRoot(Table table) {
        File ibd = new File(table.getDatabase().getDbFolder(), table.getName() + ".ibd");
        try (FileInputStream fileInputStream = new FileInputStream(ibd)) {
            byte[] pageByte = ConstantSize.PAGE.emptyBuff();
            if (fileInputStream.read(pageByte) != pageByte.length) {
                throw new IllegalArgumentException("idb file error [ " + ibd.getAbsoluteFile() + " ]");
            }
            return (RootPage) PageFactory.swap(pageByte, table);
        } catch (IOException e) {
            throw new RuntimeIoException(e);
        }
    }

    public static InnodbUserRecord getNextUserRecord(InnoDbPage page, UserRecord userRecord) {
        if (userRecord instanceof Supremum) {
            throw new NullPointerException("supremum is max record in page");
        }
        int nextRecordOffset = userRecord.nextRecordOffset();
        return RowFormatFactory.readCompactInPage(page, nextRecordOffset, userRecord.belongTo());
    }


}
