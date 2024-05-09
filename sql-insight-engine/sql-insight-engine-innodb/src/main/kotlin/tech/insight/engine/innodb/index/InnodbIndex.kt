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
package tech.insight.engine.innodb.index

import tech.insight.core.bean.Table
import tech.insight.core.logging.Logging
import tech.insight.engine.innodb.core.InnodbBTree
import tech.insight.engine.innodb.page.InnoDbPage
import tech.insight.engine.innodb.utils.PageSupport

/**
 * @author gongxuanzhangmelt@gmail.com
 */
abstract class InnodbIndex : Logging(), InnodbBTree {

    lateinit var table: Table

    override fun belongTo(): Table {
        return table
    }

    /**
     * get the b+ trees root node.
     * page is node
     *
     * @return not null
     */
    val rootPage: InnoDbPage by lazy { PageSupport.getRoot(this) }

}
