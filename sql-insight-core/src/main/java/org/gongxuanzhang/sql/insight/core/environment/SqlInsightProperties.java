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

package org.gongxuanzhang.sql.insight.core.environment;

import org.gongxuanzhang.sql.insight.core.exception.RuntimeFileNotFoundException;
import org.gongxuanzhang.sql.insight.core.exception.RuntimeIoException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * like my.cnf
 * default file path classpath:mysql.properties
 *
 * @author gongxuanzhangmelt@gmail.com
 **/
public class SqlInsightProperties extends Properties {

    private static final String CONFIG_FILE_NAME;

    static {
        CONFIG_FILE_NAME = System.getProperty("defaults-file", "/mysql.properties");
    }


    public SqlInsightProperties() {
        InputStream inputStream = getClass().getResourceAsStream(CONFIG_FILE_NAME);
        if (inputStream == null) {
            throw new RuntimeFileNotFoundException();
        }
        try {
            load(inputStream);
        } catch (IOException e) {
            throw new RuntimeIoException(e);
        }
    }


}
