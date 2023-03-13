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

import org.springframework.util.Assert;

/**
 * @author gxz gongxuanzhangmelt@gmail.com
 **/
public class BitUtils {


    private BitUtils() {
        throw new IllegalArgumentException("不支持");
    }

    /**
     * 把一个int转换成想要的字节数组
     * 最多切成4个字节
     *
     * @param integer 数字
     * @param bitSize 目标的字节数组长度
     * @return 字节数组
     **/
    public static byte[] cutToByteArray(Integer integer, int bitSize) {
        Assert.state(bitSize <= 4, "integer最多切成4个字节");
        Assert.state(bitSize >= 1, "integer至少切成1个字节");
        if (bitSize == 1) {
            return new byte[]{integer.byteValue()};
        }
        byte[] result = new byte[bitSize];
        final int base = 0xff;
        for (int size = bitSize - 1; size >= 0; size--) {
            result[size] = (byte) (integer & base);
            integer >>= 8;
        }
        return result;
    }

    public static byte[] cutToByteArray(Long aLong, int bitSize) {
        Assert.state(bitSize <= 8, "long最多切成8个字节");
        Assert.state(bitSize >= 1, "long至少切成1个字节");
        if (bitSize == 1) {
            return new byte[]{aLong.byteValue()};
        }
        byte[] result = new byte[bitSize];
        final int base = 0xffff;
        for (int size = bitSize - 1; size >= 0; size--) {
            result[size] = (byte) (aLong & base);
            aLong >>= 8;
        }
        return result;
    }


}
