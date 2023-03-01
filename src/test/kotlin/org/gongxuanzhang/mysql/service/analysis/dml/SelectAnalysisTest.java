/*
 * Copyright 2023 java-mysql  and the original author or authors <gongxuanzhangmelt@gmail.com>.
 *
 * Licensed under the GNU Affero General Public License v3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://github.com/implement-study/java-mysql/blob/main/LICENSE
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.gongxuanzhang.mysql.service.analysis.dml;

import org.gongxuanzhang.mysql.connection.Connection;
import org.gongxuanzhang.mysql.core.result.Result;
import org.gongxuanzhang.mysql.exception.MySQLException;
import org.gongxuanzhang.mysql.service.analysis.ast.SubSqlAnalysis;
import org.gongxuanzhang.mysql.service.executor.Executor;
import org.gongxuanzhang.mysql.service.token.SqlToken;
import org.gongxuanzhang.mysql.service.token.SqlTokenizer;
import org.gongxuanzhang.mysql.tool.Console;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;


@SpringBootTest
class SelectAnalysisTest {


    @Test
    public void testAnalysisSingleAs(@Autowired Connection connection) {
        String sql = "select a as b,s as d from aa.user";
        connection.execute(sql);
    }

    @Test
    public void testAnalysisPlusAs(@Autowired Connection connection) {
        String sql = "select a as b,s as d ,* ,b from aa.user";
        connection.execute(sql);
    }

    @Test
    public void testAnalysisWhere(@Autowired Connection connection) {
        String sql = "select * from aa.user where 1=1 and id>1";
        Result execute = connection.execute(sql);
    }


    @Test
    public void testAnalysisOrderSingle(@Autowired SubSqlAnalysis subSqlAnalysis) throws MySQLException {
        String sql = "select * from aa.user order by id";
        SqlTokenizer sqlTokenizer = new SqlTokenizer(sql);
        List<SqlToken> process = sqlTokenizer.process();
        Executor analysis = subSqlAnalysis.analysis(process);
    }

    @Test
    public void testAnalysisOrderAndWhere(@Autowired SubSqlAnalysis subSqlAnalysis) throws MySQLException {
        String sql = "select * from aa.user where 1=1 order by id";
        SqlTokenizer sqlTokenizer = new SqlTokenizer(sql);
        List<SqlToken> process = sqlTokenizer.process();
        Executor analysis = subSqlAnalysis.analysis(process);
    }


    @Test
    public void testAnalysisOrderTargetOrder(@Autowired SubSqlAnalysis subSqlAnalysis) throws MySQLException {
        String sql = "select * from aa.user order by id desc";
        SqlTokenizer sqlTokenizer = new SqlTokenizer(sql);
        List<SqlToken> process = sqlTokenizer.process();
        Executor analysis = subSqlAnalysis.analysis(process);


    }

    @Test
    public void testAnalysisMultiOrder(@Autowired Connection connection) {
        String sql = "select * from aa.user order by id name";
    }

    @Test
    public void testAnalysisOrderComplex(@Autowired Connection connection) {
        String sql = "select * from aa.user order by id desc, name asc,age";
    }

    @Test
    public void testAnalysisOrder(@Autowired Connection connection) {
        String sql = "select * from aa.user order by id desc";
        Console.infoResult(connection.execute(sql));
    }


}
