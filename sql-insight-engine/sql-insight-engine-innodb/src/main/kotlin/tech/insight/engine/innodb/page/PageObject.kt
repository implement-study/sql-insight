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
package tech.insight.engine.innodb.page

import tech.insight.buffer.SerializableObject
import tech.insight.engine.innodb.core.Lengthable


/**
 * innodb engine page object
 *
 * @author gongxuanzhangmelt@gmail.com
 */
interface PageObject : Lengthable, SerializableObject {

    /**
     *  page that the object belongs to
     */
    val parentPage: InnoDbPage

}
