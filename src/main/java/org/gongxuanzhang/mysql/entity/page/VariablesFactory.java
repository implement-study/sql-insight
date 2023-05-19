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

package org.gongxuanzhang.mysql.entity.page;

/**
 * Variables 用户组 工厂 {@link Variables}
 *
 * @author gxz gongxuanzhang@foxmail.com
 * @see Compact
 **/
public class VariablesFactory implements ByteBeanFactory<Variables> {


    @Override
    public Variables swap(byte[] bytes) {
        Variables bean = new Variables(bytes);
        return bean;
    }

    @Override
    public Variables create() {
        return new Variables(new byte[0]);
    }

}
