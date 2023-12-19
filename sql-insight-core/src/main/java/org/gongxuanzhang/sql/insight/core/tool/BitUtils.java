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

package org.gongxuanzhang.sql.insight.core.tool;

import org.springframework.util.Assert;

/**
 * @author gxz gongxuanzhangmelt@gmail.com
 **/
public class BitUtils {


    private BitUtils() {
        throw new IllegalArgumentException();
    }


    public static byte[] cutToByteArray(Integer integer, int byteSize) {
        Assert.state(byteSize <= 4 && byteSize >= 1, "int split byte size must between 1 and 4");
        if (byteSize == 1) {
            return new byte[]{integer.byteValue()};
        }
        byte[] result = new byte[byteSize];
        final int base = 0xff;
        for (int size = byteSize - 1; size >= 0; size--) {
            result[size] = (byte) (integer & base);
            integer >>= 8;
        }
        return result;
    }

    public static int byteArrayToInt(byte[] byteArray) {
        if (byteArray.length != Integer.BYTES) {
            throw new IllegalArgumentException("byte array length must be " + Integer.BYTES);
        }
        return ((byteArray[0] & 0xFF) << 24) |
                ((byteArray[1] & 0xFF) << 16) |
                ((byteArray[2] & 0xFF) << 8) |
                (byteArray[3] & 0xFF);
    }


}
