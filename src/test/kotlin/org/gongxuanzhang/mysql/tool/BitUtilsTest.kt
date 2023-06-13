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

package org.gongxuanzhang.mysql.tool

import org.junit.jupiter.api.Test


/**
 *
 * test for BitUtils
 *
 * @author gxz gongxuanzhangmelt@gmail.com
 **/
class BitUtilsTest {


    @Test
    fun bitTest(){
        val int = 0x0bcd1234
        val cutByteArray = BitUtils.cutToByteArray(int, 3)
        assert(cutByteArray[0] == 0xcd.toByte())
        assert(cutByteArray[1] == 0x12.toByte())
        assert(cutByteArray[2] == 0x34.toByte())
    }

    @Test
    fun bitTest1(){
        val int = 0x0bcd1234
        val cutByteArray = BitUtils.cutToByteArray(int, 2)
        assert(cutByteArray[0] == 0x12.toByte())
        assert(cutByteArray[1] == 0x34.toByte())
    }

    @Test
    fun bitTest2(){
        val int = 0x0bcd1234
        val cutByteArray = BitUtils.cutToByteArray(int, 4)
        assert(cutByteArray[0] == 0x0b.toByte())
        assert(cutByteArray[1] == 0xcd.toByte())
        assert(cutByteArray[2] == 0x12.toByte())
        assert(cutByteArray[3] == 0x34.toByte())
    }

    @Test
    fun bitTest3(){
        val long = 0x0123456789abcdef
        val cutByteArray = BitUtils.cutToByteArray(long, 8)
        assert(cutByteArray[0] == 0x01.toByte())
        assert(cutByteArray[1] == 0x23.toByte())
        assert(cutByteArray[2] == 0x45.toByte())
        assert(cutByteArray[3] == 0x67.toByte())
        assert(cutByteArray[4] == 0x89.toByte())
        assert(cutByteArray[5] == 0xab.toByte())
        assert(cutByteArray[6] == 0xcd.toByte())
        assert(cutByteArray[7] == 0xef.toByte())
    }

    @Test
    fun bitTest4(){
        val short = 0x451f.toShort()
        var cutByteArray = BitUtils.cutToByteArray(short,1)
        assert(cutByteArray[0] == 0x1f.toByte())
        cutByteArray = BitUtils.cutToByteArray(short,2)
        assert(cutByteArray[0] == 0x45.toByte())
        assert(cutByteArray[1] == 0x1f.toByte())
    }

}
