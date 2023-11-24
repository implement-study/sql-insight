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

package org.gongxuanzhang.sql.insight.core.analysis;


import static org.gongxuanzhang.sql.insight.core.analysis.OperatorType.DCL;
import static org.gongxuanzhang.sql.insight.core.analysis.OperatorType.DDL;
import static org.gongxuanzhang.sql.insight.core.analysis.OperatorType.DML;

/**
 * type for sql
 *
 * @author gongxuanzhangmelt@gmail.com
 **/
public enum SqlType {
    CREATE(DDL),
    DROP(DDL),
    ALTER(DDL),
    RENAME(DDL),
    INSERT(DML),
    UPDATE(DML),
    DELETE(DML),
    SELECT(DML),
    TRUNCATE(DML),
    SHOW(DCL),
    SET(DCL);


    private final OperatorType type;

    SqlType(OperatorType type) {
        this.type = type;
    }

    public OperatorType operatorType() {
        return type;
    }


}
