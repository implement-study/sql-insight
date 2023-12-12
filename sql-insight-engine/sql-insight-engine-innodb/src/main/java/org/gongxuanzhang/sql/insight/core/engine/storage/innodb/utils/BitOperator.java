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

package org.gongxuanzhang.sql.insight.core.engine.storage.innodb.utils;

/**
 * bit operator
 *
 * @author gongxuanzhangmelt@gmail.com
 **/
@Deprecated
public class BitOperator {

    private BitOperator() {

    }


    /**
     * set a bit from byte to one
     *
     * @param origin the byte
     * @param n      bit index,  right end is 0
     * @return number after update
     **/
    public static byte setBitToOne(byte origin, int n) {
        checkSize(n, Byte.SIZE);
        byte mask = (byte) (1 << n);
        return (byte) (origin | mask);
    }


    /**
     * like {@link BitOperator#setBitToOne(byte, int)}
     **/
    public static short setBitToOne(short origin, int n) {
        checkSize(n, Short.SIZE);
        short mask = (short) (1 << n);
        return (short) (origin | mask);
    }

    /**
     * like {@link BitOperator#setBitToOne(byte, int)}
     **/
    public static int setBitToOne(int origin, int n) {
        checkSize(n, Integer.SIZE);
        int mask = 1 << n;
        return (origin | mask);
    }

    /**
     * like {@link BitOperator#setBitToOne(byte, int)}
     **/
    public static long setBitToOne(long origin, int n) {
        checkSize(n, Long.SIZE);
        long mask = 1L << n;
        return origin | mask;
    }


    /**
     * set a bit from byte to zero
     *
     * @param origin the byte
     * @param n      bit index,  right end is 0
     * @return number after update
     **/
    public static byte setBitToZero(byte origin, int n) {
        checkSize(n, Byte.SIZE);
        byte mask = (byte) ~(1 << n);
        return (byte) (origin & mask);
    }


    /**
     * like {@link BitOperator#setBitToZero(byte, int)}
     **/
    public static short setBitToZero(short origin, int n) {
        checkSize(n, Short.SIZE);
        short mask = (short) ~(1 << n);
        return (short) (origin & mask);
    }

    /**
     * like {@link BitOperator#setBitToZero(byte, int)}
     **/
    public static int setBitToZero(int origin, int n) {
        checkSize(n, Integer.SIZE);
        int mask = ~(1 << n);
        return (origin & mask);
    }

    /**
     * like {@link BitOperator#setBitToZero(byte, int)}
     **/
    public static long setBitToZero(long origin, int n) {
        checkSize(n, Long.SIZE);
        long mask = ~(1L << n);
        return origin & mask;
    }


    private static void checkSize(int n, int checkSize) {
        if (n < 0 || n >= checkSize) {
            throw new IllegalArgumentException("length must between 0 and " + (checkSize - 1));
        }
    }
}
