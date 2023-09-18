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

package org.gongxuanzhang.sql.insight.core.analysis.druid;

import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlStatement;
import org.gongxuanzhang.sql.insight.core.command.Command;

/**
 * after druid parse sql
 * packaging command according to different types
 *
 * @author gongxuanzhangmelt@gmail.com
 **/
public interface DruidStatementAdaptor {


    /**
     * support to druid mysql statement
     * sole
     *
     * @return class for support
     **/
    Class<? extends MySqlStatement> supportType();


    /**
     * do adaptor
     *
     * @param mySqlStatement must equals {@link this#supportType()}
     * @return command
     **/
    Command adaptor(MySqlStatement mySqlStatement);
}
