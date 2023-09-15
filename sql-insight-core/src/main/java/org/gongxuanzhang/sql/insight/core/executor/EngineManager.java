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

package org.gongxuanzhang.sql.insight.core.executor;

import org.gongxuanzhang.sql.insight.core.storage.StorageEngine;

import java.util.List;

/**
 * @author gongxuanzhangmelt@gmail.com
 **/
public interface EngineManager extends EngineSelector {


    /**
     * get all engine
     **/
    List<StorageEngine> allEngine();


    /**
     * register with the manager
     **/
    void registerEngine(StorageEngine engine);

    /**
     * select engine from engine name
     *
     * @param engineName {@link EngineSelector#selectEngine(String)}
     * @return {@link EngineSelector#selectEngine(String)}
     **/
    @Override
    StorageEngine selectEngine(String engineName);


}
