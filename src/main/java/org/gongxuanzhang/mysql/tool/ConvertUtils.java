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

import org.gongxuanzhang.mysql.core.convert.Converter;
import org.gongxuanzhang.mysql.core.convert.IntConverter;
import org.gongxuanzhang.mysql.core.convert.TimeStampConverter;
import org.gongxuanzhang.mysql.core.convert.VarcharConverter;
import org.gongxuanzhang.mysql.entity.ColumnType;
import org.gongxuanzhang.mysql.exception.MySQLException;

import java.util.HashMap;
import java.util.Map;

/**
 * convert 工具类
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
public class ConvertUtils {

    private ConvertUtils() {

    }

    private static final Map<ColumnType, Converter<?>> CONVERTER_MAP = new HashMap<>();

    static {
        CONVERTER_MAP.put(ColumnType.INT, new IntConverter());
        CONVERTER_MAP.put(ColumnType.STRING, new VarcharConverter());
        CONVERTER_MAP.put(ColumnType.TIMESTAMP, new TimeStampConverter());

    }


    public static <T> T convert(ColumnType type, String value) throws MySQLException {
        Converter<?> converter = CONVERTER_MAP.get(type);
        if (converter == null) {
            String message = "类型[%s]值[%s]无法转换";
            throw new MySQLException(String.format(message, type, value));
        }
        return (T) converter.convert(value);
    }
}
