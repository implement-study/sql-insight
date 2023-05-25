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

package org.gongxuanzhang.mysql.core;

import org.gongxuanzhang.mysql.exception.MySQLException;

/**
 * 页选择器
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
public interface PageSelector {

    /**
     * 拿到根页
     *
     * @return 页的字节数组
     * @throws MySQLException 过程中可能出现异常
     **/
    byte[] getRootPage() throws MySQLException;

    /**
     * 拿到当前最后一页
     *
     * @return 页的字节数组
     * @throws MySQLException 过程中可能出现异常
     **/
    byte[] getLastPage() throws MySQLException;


    /**
     * 添加一个新页
     *
     * @param newPage 新页的数组
     **/
    void addNewPage(byte[] newPage) throws MySQLException;


}
