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

package org.gongxuanzhang.mysql.service.token;

import org.gongxuanzhang.mysql.exception.SqlParseException;
import org.junit.jupiter.api.Test;

import java.util.List;


class SqlTokenizerTest {

    @Test
    public void tokenizer() {
        SqlTokenizer sqlTokenizer = new SqlTokenizer("select * from aa");
        try {
            List<SqlToken> process = sqlTokenizer.process();
            System.out.println(process);
        } catch (SqlParseException e) {
            e.printStackTrace();
        }
    }

}
