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

import lombok.extern.slf4j.Slf4j;
import org.gongxuanzhang.sql.insight.core.engine.storage.innodb.index.InnodbIndex;
import org.gongxuanzhang.sql.insight.core.engine.storage.innodb.page.compact.IndexRecord;
import org.gongxuanzhang.sql.insight.core.engine.storage.innodb.page.compact.RowFormatFactory;
import org.gongxuanzhang.sql.insight.core.engine.storage.innodb.utils.RowComparator;
import org.gongxuanzhang.sql.insight.core.object.Column;
import org.gongxuanzhang.sql.insight.core.object.value.Value;

import java.util.ArrayList;
import java.util.List;

import static org.gongxuanzhang.sql.insight.core.engine.storage.innodb.factory.PageFactory.createDataPage;
import static org.gongxuanzhang.sql.insight.core.engine.storage.innodb.page.Constant.DIRECTION_COUNT_THRESHOLD;

/**
 * @author gongxuanzhangmelt@gmail.com
 **/
@Slf4j
public class DataPage extends InnoDbPage {

    public DataPage(InnodbIndex index) {
        super(index);
    }


    @Override
    protected InnodbUserRecord wrapUserRecord(int offsetInPage) {
        return RowFormatFactory.readRecordInPage(this, offsetInPage, this.ext.belongIndex.belongTo());
    }

    /**
     * data page will split when free space less than one-sixteenth page size
     **/
    @Override
    protected void splitIfNecessary() {
        if (this.getFreeSpace() > ConstantSize.PAGE.size() >> 4) {
            return;
        }
        List<InnodbUserRecord> pageUserRecord = new ArrayList<>(this.pageHeader.recordCount + 1);
        InnodbUserRecord base = this.infimum;
        int allLength = 0;
        while (true) {
            base = getUserRecordByOffset(base.offset() + base.nextRecordOffset());
            if (base == this.supremum) {
                break;
            }
            pageUserRecord.add(base);
            allLength += base.length();
        }
        //   todo non middle split ?
        if (this.pageHeader.directionCount < DIRECTION_COUNT_THRESHOLD) {
            middleSplit(pageUserRecord, allLength);
        }
    }


    /**
     * middle split.
     * insert direction unidentified (directionCount less than 5)
     * <p>
     * if this page is root page.
     * transfer root page to index page from data page.
     * create two data page linked.
     * if this page is normal leaf node,
     * create a data page append to index file and insert a index record to parent (index page)
     *
     * @param pageUserRecord all user record in page with inserted
     * @param allLength      all user record length
     **/
    private void middleSplit(List<InnodbUserRecord> pageUserRecord, int allLength) {
        int half = allLength / 2;
        DataPage firstDataPage = null;
        DataPage secondDataPage = null;
        for (int i = 0; i < pageUserRecord.size(); i++) {
            allLength -= pageUserRecord.get(i).length();
            if (allLength <= half) {
                InnodbIndex belong = this.ext.belongIndex;
                firstDataPage = createDataPage(pageUserRecord.subList(0, i), belong);
                secondDataPage = createDataPage(pageUserRecord.subList(i, pageUserRecord.size()), belong);
                break;
            }
        }
        if (firstDataPage == null) {
            throw new NullPointerException("data page error");
        }
        upgrade(firstDataPage, secondDataPage);
    }

    @Override
    public IndexRecord pageIndex() {
        InnodbUserRecord firstData = getUserRecordByOffset(infimum.offset() + infimum.nextRecordOffset());
        List<Column> columns = this.ext.belongIndex.columns();
        Value[] values = columns.stream()
                .map(Column::getName)
                .map(firstData::getValueByColumnName)
                .toArray(Value[]::new);
        return new IndexRecord(new IndexNode(values, this.fileHeader.offset), this.ext.belongIndex);
    }

    @Override
    public int compare(InnodbUserRecord o1, InnodbUserRecord o2) {
        return RowComparator.primaryKeyComparator().compare(o1, o2);
    }
}
