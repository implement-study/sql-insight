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

package org.gongxuanzhang.sql.insight.core.engine.storage.innodb.utils;

import org.springframework.util.Assert;

import java.nio.ByteBuffer;

/**
 * @author gxz gongxuanzhangmelt@gmail.com
 **/
public class BitUtils {


    private BitUtils() {
        throw new IllegalArgumentException();
    }

    /**
     * 把一个short转换成想要的字节数组
     * 从后往前切
     * 最多切成2个字节
     *
     * @param s       short 数字
     * @param bitSize 目标的字节数组长度
     * @return 字节数组
     **/
    public static byte[] cutToByteArray(Short s, int bitSize) {
        if (bitSize == 1) {
            return new byte[]{s.byteValue()};
        }
        if (bitSize == 2) {
            return new byte[]{(byte) (s >>> 8), s.byteValue()};
        }
        throw new IllegalArgumentException("short split byte size must between 1 and 2");
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

    public static byte[] cutToByteArray(Long aLong, int byteSize) {
        Assert.state(byteSize <= 8 && byteSize >= 1, "long split byte size must between 1 and 8");
        if (byteSize == 1) {
            return new byte[]{aLong.byteValue()};
        }
        byte[] result = new byte[byteSize];
        final int base = 0xffff;
        for (int size = byteSize - 1; size >= 0; size--) {
            result[size] = (byte) (aLong & base);
            aLong >>= 8;
        }
        return result;
    }


    /**
     * join a byte array to int
     * byte[N]byte[1]byte[0]
     *
     * @return int
     **/
    public static int joinInt(byte[] bytes) {
        if (bytes.length > Integer.BYTES) {
            throw new IllegalArgumentException("join int, array length must less 4");
        }
        int result = 0;
        for (byte aByte : bytes) {
            result <<= 8;
            result |= aByte;
        }
        return result;
    }

    public static long joinLong(byte[] bytes) {
        if (bytes.length > Long.BYTES) {
            throw new IllegalArgumentException("join long, array length must less 8");
        }
        long result = 0L;
        for (byte aByte : bytes) {
            result <<= 8;
            result |= aByte;
        }
        return result;
    }


    public static byte[] readLast(ByteBuffer byteBuffer, int length) {
        return readLast(byteBuffer, length, 0);
    }


    /**
     * 把一个长度为2的字节数组转成一个short
     *
     * @param shortBytes 字节数组 长度必须是2
     * @return short
     **/
    public static short swapShort(byte[] shortBytes) {
        if (shortBytes.length != 2) {
            throw new IllegalArgumentException("shortBytes 长度必须是2");
        }
        return (short) ((shortBytes[0] << 8) | (shortBytes[1] & 0xff));
    }

    /**
     * 从后读取buffer
     * 不会调整buffer中的指针
     *
     * @param byteBuffer buffer
     * @param length     长度
     * @param offset     尾部偏移
     * @return 返回长度读取的byte
     **/
    public static byte[] readLast(ByteBuffer byteBuffer, int length, int offset) {
        if (byteBuffer.capacity() < length + offset) {
            throw new IllegalArgumentException("byte buffer 没有这么长！ " + (length + offset));
        }
        byte[] resultBuffer = new byte[length];
        byteBuffer.get(resultBuffer, byteBuffer.capacity() - length - offset, length);
        return resultBuffer;
    }


    /**
     * 从buffer中读取N位 然后转成long
     **/
    public static long readLong(ByteBuffer byteBuffer, int length) {
        byte[] buffer = new byte[length];
        byteBuffer.get(buffer);
        return joinLong(buffer);
    }


}
