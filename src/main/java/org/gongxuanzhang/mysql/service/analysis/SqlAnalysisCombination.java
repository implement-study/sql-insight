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

package org.gongxuanzhang.mysql.service.analysis;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import org.gongxuanzhang.mysql.exception.MySQLException;
import org.gongxuanzhang.mysql.exception.SqlParseException;
import org.gongxuanzhang.mysql.service.executor.Executor;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author gxz gongxuanzhangmelt@gmail.com
 **/
@Component
@Primary
public class SqlAnalysisCombination implements SqlAnalysis, ApplicationContextAware {


    private Map<Class<? extends SQLStatement>, StandaloneSqlAnalysis> standaloneAnalyzerMap;

    @Override
    public Executor analysis(String sql) throws MySQLException {
        SQLStatement sqlStatement;
        try {
            sqlStatement = SQLUtils.parseSingleMysqlStatement(sql);
        } catch (Exception e) {
            throw new SqlParseException(e);
        }
        StandaloneSqlAnalysis standaloneSqlAnalysis = this.standaloneAnalyzerMap.get(sqlStatement.getClass());
        if (standaloneSqlAnalysis == null) {
            throw new MySQLException(String.format("没有支持%s类型的解析器", sqlStatement.getClass().getSimpleName()));
        }
        return standaloneSqlAnalysis.doAnalysis(sqlStatement);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Map<Class<? extends SQLStatement>, StandaloneSqlAnalysis> candidate = new HashMap<>();
        Map<String, StandaloneSqlAnalysis> analysisMap = applicationContext.getBeansOfType(StandaloneSqlAnalysis.class);
        analysisMap.forEach((beanName, analysis) -> {
            candidate.put(analysis.support(), analysis);
        });
        this.standaloneAnalyzerMap = Collections.unmodifiableMap(candidate);
    }
}
