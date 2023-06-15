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
 * @author gxz gongxuanzhang@foxmail.com
 **/
public class ArrayUtils {

    private ArrayUtils() {

    }

    public static short[] insert(short[] array, int index, short element) {
        short[] newArray = new short[array.length + 1];
        System.arraycopy(array, 0, newArray, 0, index);
        newArray[index] = element;
        for (int i = 0; i < array.length - index; i++) {
            newArray[newArray.length - i - 1] = array[array.length - i - 1];
        }
        return newArray;
    }


    /**
     * 比较两个字节数组
     * 当完全相等时返回0
     * 长度相同 自然排序
     * 长度不同 比较有内容的部分
     * 有内容的部分不同，自然排序
     * 有内容的部分相同 按长度自然排序
     **/
    public static int compare(byte[] aBytes,byte[] otherBytes){
        int length1 = aBytes.length;
        int length2 = otherBytes.length;
        int minLength = Math.min(length1, length2);
        for (int i = 0; i < minLength; i++) {
            int cmp = Byte.compare(aBytes[i], otherBytes[i]);
            if (cmp != 0) {
                return cmp;
            }
        }
        return Integer.compare(length1, length2);
    }

}
