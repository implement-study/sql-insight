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

package org.gongxuanzhang.mysql.core.result;


import org.gongxuanzhang.mysql.core.ErrorResult;
import org.gongxuanzhang.mysql.core.SessionManager;
import org.gongxuanzhang.mysql.exception.SessionException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * service返回的统一实体
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
public interface Result {

    /**
     * 返回码
     *
     * @return 如果正常返回  是200 非正常返回统一 是非200 不做区分
     **/
    int getCode();

    /**
     * 错误信息,如果正常返回没有此属性
     *
     * @return 错误信息  如果没有错误信息 是null
     **/
    String getErrorMessage();


    /**
     * sql执行时间
     *
     * @return 格式化之后的内容 用于显示
     **/
    String getSqlTime();


    /**
     * 设置sql时间
     **/
    void setSqlTime(String sqlTime);

    /**
     * 设置sql
     **/
    void setSql(String sql);

    /**
     * 执行的sql
     *
     * @return sql
     **/
    String getSql();


    /**
     * 返回简单成功
     *
     * @return 成功且没有信息
     **/
    static Result success() {
        try {
            String sql = SessionManager.currentSession().getSql();
            SuccessResult successResult = new SuccessResult();
            successResult.setSql(sql);
            return successResult;
        } catch (SessionException e) {
            return new ErrorResult("会话异常", "unknown");
        }
    }

    /**
     * 错误返回
     *
     * @param errorMessage 错误信息
     * @return 错误返回
     **/
    static Result error(String errorMessage) {
        try {
            String sql = SessionManager.currentSession().getSql();
            return new ErrorResult(errorMessage, sql);
        } catch (SessionException e) {
            return new ErrorResult("会话异常", "unknown");
        }

    }

    /**
     * 携带信息的返回值
     * 如 插入 修改时
     *
     * @param message 信息内容
     * @return 携带信息的返回值
     **/
    static Result info(String message) {
        try {
            String sql = SessionManager.currentSession().getSql();
            InfoResult infoResult = new InfoResult(message);
            infoResult.setSql(sql);
            return infoResult;
        } catch (SessionException e) {
            return new ErrorResult("会话异常", "unknown");
        }
    }

    /**
     * 返回有查询结果
     *
     * @param head     元数据头
     * @param dataList 元数据
     * @return 结果
     **/
    static Result select(List<String> head, List<Map<String,String>> dataList) {
        try {
            String sql = SessionManager.currentSession().getSql();
            SelectResult selectResult = new SelectResult(head, dataList);
            selectResult.setSql(sql);
            return selectResult;
        } catch (SessionException e) {
            return new ErrorResult("会话异常", "unknown");
        }
    }




    /**
     * 返回单列结果
     *
     * @param head     列标题
     * @param dataList 列数据
     * @return 结果
     **/
    static Result singleRow(String head, List<String> dataList) {
        if (dataList == null) {
            dataList = new ArrayList<>();
        }
        SingleRowResult singleRowResult = new SingleRowResult(head, dataList);
        try {
            String sql = SessionManager.currentSession().getSql();
            singleRowResult.setSql(sql);
            return singleRowResult;
        } catch (SessionException e) {
            return new ErrorResult("会话异常", "unknown");
        }
    }


}
