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

package org.gongxuanzhang.mysql.tool;

/**
 * 置位的工具类
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
public class BitSetter {

    private BitSetter() {

    }


    /**
     * 把一个数字的第某位置为1
     *
     * @param origin 原数字
     * @param n      修改第几位  从左到右分别是 76543210
     * @return 修改之后的数字
     **/
    public static byte setBitToOne(byte origin, int n) {
        checkSize(n, 8);
        byte mask = (byte) (1 << n);
        return (byte) (origin | mask);
    }


    /**
     * 同 {@link BitSetter#setBitToOne(byte, int)}
     **/
    public static short setBitToOne(short origin, int n) {
        checkSize(n, 16);
        short mask = (short) (1 << n);
        return (short) (origin | mask);
    }

    /**
     * 同 {@link BitSetter#setBitToOne(byte, int)}
     **/
    public static int setBitToOne(int origin, int n) {
        checkSize(n, 32);
        int mask = 1 << n;
        return (origin | mask);
    }

    /**
     * 同 {@link BitSetter#setBitToOne(byte, int)}
     **/
    public static long setBitToOne(long origin, int n) {
        checkSize(n, 64);
        long mask = 1L << n;
        return origin | mask;
    }


    /**
     * 把一个数字的第某位置为0
     *
     * @param origin 原数字
     * @param n      修改第几位  从左到右分别是 76543210
     * @return 修改之后的数字
     **/
    public static byte setBitToZero(byte origin, int n) {
        checkSize(n, 8);
        byte mask = (byte) ~(1 << n);
        return (byte) (origin & mask);
    }


    /**
     * 同 {@link BitSetter#setBitToZero(byte, int)}
     **/
    public static short setBitToZero(short origin, int n) {
        checkSize(n, 16);
        short mask = (short) ~(1 << n);
        return (short) (origin & mask);
    }

    /**
     * 同 {@link BitSetter#setBitToZero(byte, int)}
     **/
    public static int setBitToZero(int origin, int n) {
        checkSize(n, 32);
        int mask = ~(1 << n);
        return (origin & mask);
    }

    /**
     * 同 {@link BitSetter#setBitToZero(byte, int)}
     **/
    public static long setBitToZero(long origin, int n) {
        checkSize(n, 64);
        long mask = ~(1L << n);
        return origin & mask;
    }


    private static void checkSize(int n, int checkSize) {
        if (n < 0 || n >= checkSize) {
            throw new IllegalArgumentException("长度只能在0到" + (checkSize - 1) + "之间");
        }
    }
}
