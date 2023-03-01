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

package org.gongxuanzhang.mysql.storage;

import org.gongxuanzhang.mysql.annotation.DependOnContext;
import org.gongxuanzhang.mysql.storage.fool.Fool;
import org.gongxuanzhang.mysql.storage.innodb.InnoDb;

/**
 * 存储引擎接口
 * 默认实现有Innodb，Fool
 * 可以自定义
 *
 * @author gxz gongxuanzhang@foxmail.com
 * @see Fool
 * @see InnoDb
 **/
@DependOnContext
public interface StorageEngine extends CreateTableEngine, InsertEngine,
        DeleteEngine, SelectEngine, UpdateEngine, TruncateEngine {


    /**
     * 引擎名称
     *
     * @return name
     **/
    String getEngineName();

    /**
     * 引擎是否支持事务
     *
     * @return true 是支持
     **/
    boolean supportTransaction();


}
