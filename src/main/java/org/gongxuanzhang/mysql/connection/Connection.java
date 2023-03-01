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

package org.gongxuanzhang.mysql.connection;

import org.gongxuanzhang.mysql.core.MySqlEngine;
import org.gongxuanzhang.mysql.core.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 模拟连接 只为了传递sql
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
@RestController
public class Connection {

    @Autowired
    MySqlEngine mySqlEngine;


    @PostMapping("/execute")
    public Result execute(String sql) {
        return mySqlEngine.doSql(sql);
    }

//    @GetMapping("/openSession")
//    public Result openSession(){
//        MySqlSession mySqlSession = SessionManager.currentSession();
//    }

}
