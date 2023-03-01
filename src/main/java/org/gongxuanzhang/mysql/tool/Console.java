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

package org.gongxuanzhang.mysql.tool;

import com.alibaba.fastjson2.JSONObject;
import org.gongxuanzhang.mysql.core.ErrorResult;
import org.gongxuanzhang.mysql.core.result.Result;
import org.gongxuanzhang.mysql.core.result.SelectResult;
import org.gongxuanzhang.mysql.core.result.SuccessResult;

import java.util.List;

/**
 * @author gxz gongxuanzhang@foxmail.com
 **/
public class Console {

    public static void infoResult(Result result) {
        if (result instanceof SelectResult) {
            selectInfo((SelectResult) result);
        } else if (result instanceof ErrorResult) {
            System.err.println(result.getErrorMessage());
        } else if (result instanceof SuccessResult) {
            System.out.println("成功执行");
        }

    }

    private static void selectInfo(SelectResult selectResult) {
        List<JSONObject> data = selectResult.getData();
        data.forEach(System.out::println);
    }

}
