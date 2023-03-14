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

package org.gongxuanzhang.mysql.tool;

import org.gongxuanzhang.mysql.exception.SqlParseException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * sql解析相关的工具类
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
public class SqlUtils {

    private SqlUtils() {

    }

    private static final Pattern ILLEGAL_PATTERN = Pattern.compile("[^\\w]+");


    /**
     * 校验变量名的合法行
     *
     * @param varName 变量名  可以是表名，变量名，数据库名等
     **/
    public static void checkVarName(String varName) throws SqlParseException {
        Matcher matcher = ILLEGAL_PATTERN.matcher(varName);
        if (matcher.find()) {
            throw new SqlParseException("变量名[" + varName + "]非法,只能有字母数字下划线");
        }
    }


    /**
     * 计算器执行时间
     *
     * @param startTime 开始时间
     * @return double小数的秒
     **/
    public static String sqlTime(long startTime) {
        return String.format("%.3f s", (System.currentTimeMillis() - startTime) / 1000.0);
    }


}
