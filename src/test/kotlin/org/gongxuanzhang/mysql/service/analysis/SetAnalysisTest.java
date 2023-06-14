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

package org.gongxuanzhang.mysql.service.analysis;

import org.gongxuanzhang.mysql.exception.MySQLException;
import org.gongxuanzhang.mysql.service.analysis.session.SetAnalysis;
import org.gongxuanzhang.mysql.service.executor.Executor;
import org.gongxuanzhang.mysql.service.token.SqlTokenizer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;


class SetAnalysisTest {

    String sql;

    @AfterEach
    public void analysis() throws MySQLException {
        SqlTokenizer tokenStream = new SqlTokenizer(sql);
        SetAnalysis setAnalysis = new SetAnalysis();
        Executor analysis = setAnalysis.analysis(tokenStream.process());
    }


    @Test
    public void analysisTest1() throws MySQLException {
        sql = "set @@a='1'";
    }

    @Test
    public void analysisTest2() throws MySQLException {
        sql = "set  @a='1'";
    }

    @Test
    public void analysisTest3() throws MySQLException {
        sql = "set @global.a='1'";
    }

    @Test
    public void analysisTest4() throws MySQLException {
        sql = "set @session.a='1'";
    }

    @Test
    public void analysisTest5() throws MySQLException {
        sql = "set @@session.a='1'";
    }

    @Test
    public void analysisTest6() throws MySQLException {
        sql = "set @@global.a='1'";
    }

    @Test
    public void analysisTest7() throws MySQLException {
        sql = "set global a='1'";
    }

    @Test
    public void analysisTest8() throws MySQLException {
        sql = "set session a='1'";
    }

    @Test
    public void analysisTest9() throws MySQLException {
        sql = "set  a='1'";
    }

}
