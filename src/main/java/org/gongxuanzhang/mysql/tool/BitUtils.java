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

import java.nio.ByteBuffer;

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


    /**
     * 把一个不定长的字节数组组成一个int
     *
     * @param bytes 最长是4
     * @return 返回int
     **/
    public static int joinInt(byte[] bytes) {
        if (bytes.length > 4) {
            throw new IllegalArgumentException("拼接int数组最长为4");
        }
        int result = 0;
        for (byte aByte : bytes) {
            result <<= 8;
            result |= aByte;
        }
        return result;
    }

    /**
     * 把一个不定长的字节数组组成一个long
     *
     * @param bytes 最长是8
     * @return 返回long
     **/
    public static long joinLong(byte[] bytes) {
        if (bytes.length > 8) {
            throw new IllegalArgumentException("拼接long数组最长为8");
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


}
