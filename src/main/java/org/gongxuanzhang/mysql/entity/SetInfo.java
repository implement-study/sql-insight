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

import org.gongxuanzhang.mysql.tool.Pair;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;

/**
 * set info
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
public class SetInfo implements ExecuteInfo, Iterable<Pair<String, Function<String, ?>>> {

    //  todo 暂时写成 String  以后要变成cell

    private final List<Pair<String, Function<String, ?>>> setList = new ArrayList<>();


    public void addSet(String colName, String value) {
        setList.add(Pair.of(colName, (arbitrary) -> value));
    }

    public void addSet(String colName, Function<String, ?> function) {
        setList.add(Pair.of(colName, function));
    }


    @Override
    public Iterator<Pair<String, Function<String, ?>>> iterator() {
        return setList.iterator();
    }
}
