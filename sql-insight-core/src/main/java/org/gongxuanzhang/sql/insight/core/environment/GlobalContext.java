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
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * @author gongxuanzhangmelt@gmail.com
 **/
public class GlobalContext
        extends AbstractMapContext {

    private GlobalContext(Map<String, String> map) {
        super(map);
    }


    private static final GlobalContext INSTANCE = createGlobalContext();

    private static GlobalContext createGlobalContext() {
        String configFileName = System.getProperty("default-file", "/mysql.properties");
        InputStream inputStream = GlobalContext.class.getResourceAsStream(configFileName);
        if (inputStream == null) {
            throw new RuntimeFileNotFoundException("check your default-file property " + configFileName);
        }
        try {
            Properties config = new Properties();
            config.load(inputStream);
            Map<String, String> map = new HashMap<>(config.size() / 3 * 4 + 1);
            config.forEach((k, v) -> map.put(k.toString(), v.toString()));
            return new GlobalContext(map);
        } catch (IOException e) {
            throw new RuntimeIoException(e);
        }
    }

    public static GlobalContext getInstance() {
        return INSTANCE;
    }

}
