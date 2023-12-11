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

package org.gongxuanzhang.sql.insight.core.object;

/**
 * @author gongxuanzhangmelt@gmail.com
 **/
public interface RowFormat {

    /**
     * name for row format
     **/
    String getName();


    /**
     * how to format row to byte array
     **/
    byte[] rowBytes();

    /**
     * row format is ni physics row in disk.
     * user record is logic and more info.
     * this method meaning physics module to logic module
     *
     * @return user record
     **/
    UserRecord toUserRecord();
}
