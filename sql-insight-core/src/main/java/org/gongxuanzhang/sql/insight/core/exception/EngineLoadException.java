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

package org.gongxuanzhang.sql.insight.core.exception;

/**
 * in spi loading engine throws
 *
 * @author gongxuanzhangmelt@gmail.com
 **/
public class EngineLoadException extends SqlInsightException {


    public EngineLoadException(ClassNotFoundException e, String className) {
        super("can't found class " + className);
    }

    public EngineLoadException(NoSuchMethodException e, String className) {
        super(className + " must have a not param constructor method");
    }

    public EngineLoadException(ReflectiveOperationException e) {
        super(e);
    }


}
