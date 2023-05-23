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

package org.gongxuanzhang.mysql.entity;


import org.gongxuanzhang.mysql.entity.page.UserRecord;
import org.gongxuanzhang.mysql.exception.MySQLException;

/**
 * 插入行，cellList中永远保存表中所有内容
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
public interface InsertRow extends Row {


    /**
     * 插入行转换成用户行
     *
     * @param recordType 用户行的类型
     * @return 具体的用户行
     **/
    <R extends UserRecord> R toUserRecord(Class<R> recordType) throws MySQLException;

}
