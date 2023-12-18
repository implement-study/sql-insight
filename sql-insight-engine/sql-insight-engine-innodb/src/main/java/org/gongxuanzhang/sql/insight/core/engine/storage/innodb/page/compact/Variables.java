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

package org.gongxuanzhang.sql.insight.core.engine.storage.innodb.page.compact;


import org.gongxuanzhang.easybyte.core.ByteWrapper;
import org.gongxuanzhang.sql.insight.core.engine.storage.innodb.page.PageObject;

/**
 * variable data type like varchar
 *
 * @author gongxuanzhang
 **/
public class Variables implements ByteWrapper, PageObject {

    byte[] varBytes;

    public Variables() {
        this.varBytes = new byte[0];
    }

    public void addVariableLength(byte length) {
        if (this.varBytes.length == 0) {
            this.varBytes = new byte[]{length};
            return;
        }
        byte[] newBytes = new byte[varBytes.length + 1];
        System.arraycopy(varBytes, 0, newBytes, 1, varBytes.length);
        newBytes[0] = length;
        this.varBytes = newBytes;
    }

    public int variableLength() {
        int sumLength = 0;
        for (byte varByte : this.varBytes) {
            sumLength += varByte;
        }
        return sumLength;
    }

    @Override
    public byte[] toBytes() {
        return this.varBytes;
    }

    @Override
    public int length() {
        return this.varBytes.length;
    }
}
