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

package org.gongxuanzhang.sql.insight.core.object;


import java.util.Comparator;

/**
 * @author gongxuanzhangmelt@gmail.com
 **/
public class OrderBy implements Comparator<Row> {

    private final String[] column;

    private final boolean[] asc;

    private final Comparator<Row> comparator;

    public OrderBy(String[] column, boolean[] asc) {
        this.column = column;
        this.asc = asc;
        this.comparator = createComparator();
    }

    private Comparator<Row> createComparator() {
        return (r1, r2) -> {
            for (int i = 0; i < column.length; i++) {
                String col = column[i];
                int val = r1.getValueByColumnName(col).compareTo(r2.getValueByColumnName(col));
                if (val != 0) {
                    return asc[i] ? val : -val;
                }
            }
            return 0;
        };
    }


    @Override
    public int compare(Row o1, Row o2) {
        return comparator.compare(o1, o2);
    }
}
