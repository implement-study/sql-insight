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

package org.gongxuanzhang.mysql.tool;

import org.gongxuanzhang.mysql.exception.SqlParseException;

import java.util.function.Supplier;

/**
 * check when sql analysis
 * throw {@link SqlParseException}
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
public class SqlAssert {


    public static void isTure(boolean expect, String message) throws SqlParseException {
        if (!expect) {
            throw new SqlParseException(message);
        }
    }

    public static void isTure(boolean expect, Supplier<String> messageSupplier) throws SqlParseException {
        if (!expect) {
            throw new SqlParseException(messageSupplier.get());
        }
    }

    /**
     * target in [min,max]
     **/
    public static void between(int min, int max, int target) throws SqlParseException {
        if (target < min || target > max) {
            String message = String.format("[%s]必须在[%s]和[%s]之间", target, min, max);
            throw new SqlParseException(message);
        }
    }


}
