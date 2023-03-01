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

package org.gongxuanzhang.mysql.core.result;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

/**
 * 携带信息的返回值
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
@EqualsAndHashCode(callSuper = true)
@Data
@Slf4j
public class InfoResult extends SuccessResult {

    private String message;

    public InfoResult(String message) {
        log.info(message);
        this.message = message;
    }

    @Override
    public int getCode() {
        return 200;
    }

}
