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

}