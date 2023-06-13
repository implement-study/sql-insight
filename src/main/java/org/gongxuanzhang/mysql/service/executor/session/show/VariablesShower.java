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

package org.gongxuanzhang.mysql.service.executor.session.show;

import org.gongxuanzhang.mysql.core.MySqlSession;
import org.gongxuanzhang.mysql.core.SessionManager;
import org.gongxuanzhang.mysql.core.result.Result;
import org.gongxuanzhang.mysql.entity.GlobalProperties;
import org.gongxuanzhang.mysql.entity.ShowVarInfo;
import org.gongxuanzhang.mysql.exception.MySQLException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.gongxuanzhang.mysql.tool.ExceptionThrower.errorSwap;

/**
 * show variables
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
public class VariablesShower implements Shower {

    private final static List<String> HEAD = new ArrayList<>();

    private final ShowVarInfo info;

    static {
        HEAD.add("variable_name");
        HEAD.add("value");
    }

    public VariablesShower(ShowVarInfo info) {
        this.info = info;
    }

    @Override
    public Result show() throws MySQLException {
        if (info.isGlobal()) {
            return globalShow();
        }
        if (info.isSession()) {
            return sessionShow();
        }
        return defaultShow();
    }

    private Result defaultShow() throws MySQLException {
        try {
            MySqlSession mySqlSession = SessionManager.currentSession();
            GlobalProperties instance = GlobalProperties.getInstance();
            Map<String, String> attr = new HashMap<>(mySqlSession.getAllAttr());
            instance.getAllAttr().forEach(attr::putIfAbsent);
            return returnVar(attr);
        } catch (Exception e) {
            return errorSwap(e);
        }
    }

    private Result globalShow() throws MySQLException {
        try {
            GlobalProperties instance = GlobalProperties.getInstance();
            return returnVar(instance.getAllAttr());
        } catch (Exception e) {
            return errorSwap(e);
        }
    }

    private Result sessionShow() throws MySQLException {
        try {
            MySqlSession mySqlSession = SessionManager.currentSession();
            return returnVar(mySqlSession.getAllAttr());
        } catch (Exception e) {
            return errorSwap(e);
        }
    }

    private Result returnVar(Map<String, String> allAttr) {
        List<Map<String, String>> dataList = new ArrayList<>();
        allAttr.forEach((k, v) -> {
            Map<String, String> data = new HashMap<>(8);
            data.put(HEAD.get(0), k);
            data.put(HEAD.get(1), v);
            dataList.add(data);
        });
        return Result.select(HEAD, dataList);
    }
}
