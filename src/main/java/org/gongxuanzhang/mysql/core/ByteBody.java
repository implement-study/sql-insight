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

package org.gongxuanzhang.mysql.core;

import java.util.ArrayList;
import java.util.List;

/**
 * 为了方便使用Byte byte数组产生的类
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
public class ByteBody {

    private final List<Byte> body;

    public ByteBody() {
        this.body = new ArrayList<>();
    }

    public ByteBody(byte[] bytes) {
        this.body = new ArrayList<>(bytes.length);
        for (byte aByte : bytes) {
            this.body.add(aByte);
        }
    }

    public void add(byte b) {
        this.body.add(b);
    }

    public void add(byte[] body) {
        for (byte b : body) {
            this.body.add(b);
        }
    }

    public byte[] toArray() {
        byte[] body = new byte[this.body.size()];
        for (int i = 0; i < body.length; i++) {
            body[i] = this.body.get(i);
        }
        return body;
    }

}
