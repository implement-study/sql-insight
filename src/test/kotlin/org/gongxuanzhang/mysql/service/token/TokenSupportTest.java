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

import org.gongxuanzhang.mysql.exception.MySQLException;
import org.junit.jupiter.api.Test;


class TokenSupportTest {

    @Test
    public void testChain() throws MySQLException {
        TokenSupport.token(new SqlToken(TokenKind.DOUBLE_AT, "@@")).when(TokenKind.NE).then(() -> {
            System.out.println("这是ne");
        }).when(TokenKind.DOUBLE_AT).then(() -> {
            System.out.println("这是双dou");
        }).get();
    }

}
