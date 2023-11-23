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


import com.alibaba.druid.sql.ast.SQLDataType;
import com.alibaba.druid.sql.ast.statement.SQLCharacterDataType;
import lombok.Data;

/**
 * @author gongxuanzhangmelt@gmail.com
 **/
@Data
public class DataType implements FillDataVisitor {

    private Type type;

    private int length;


    @Override
    public void endVisit(SQLDataType x) {
        type = Type.valueOf(x.getName().toUpperCase());
        this.length = type.defaultLength;
    }


    @Override
    public void endVisit(SQLCharacterDataType x) {
        type = Type.valueOf(x.getName().toUpperCase());
        this.length = x.getLength();
        if (this.length < 0) {
            this.length = type.defaultLength;
        }
    }


    public enum Type {
        INT(4), VARCHAR(255), CHAR(255), TIME(-1);


        final int defaultLength;

        Type(int defaultLength) {
            this.defaultLength = defaultLength;
        }
    }


}
