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

package org.gongxuanzhang.mysql.entity;

import lombok.Data;
import org.gongxuanzhang.mysql.annotation.DependOnContext;
import org.gongxuanzhang.mysql.tool.Context;

import java.io.File;


/**
 * 数据库信息
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
@Data
public class DatabaseInfo implements ExecuteInfo {


    private final String databaseName;

    public DatabaseInfo(String databaseName) {
        this.databaseName = databaseName;
    }


    /**
     * 返回数据库目标位置文件夹
     **/
    @DependOnContext
    public File sourceFile() {
        File home = Context.getHome();
        return new File(home, databaseName);
    }

    @Override
    public String toString() {
        return databaseName;
    }
}
