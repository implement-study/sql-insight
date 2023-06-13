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

import org.gongxuanzhang.mysql.core.ErrorResult;
import org.gongxuanzhang.mysql.core.result.Result;
import org.gongxuanzhang.mysql.core.result.SelectResult;
import org.gongxuanzhang.mysql.core.result.SuccessResult;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * @author gxz gongxuanzhang@foxmail.com
 **/
public class Console {

    private static final Pattern CHINESE_CHARACTERS_PATTERN = Pattern.compile("[\\u4E00-\\u9FA5\\uF900-\\uFA2D]+");

    public static void infoResult(Result result) {
        if (result instanceof SelectResult) {
            selectInfo((SelectResult) result);
        } else if (result instanceof ErrorResult) {
            System.err.println(result.getErrorMessage());
        } else if (result instanceof SuccessResult) {
            System.out.println("成功执行");
        }

    }

    private static void selectInfo(SelectResult selectResult) {
        List<Map<String, String>> data = selectResult.getData();
        List<String> head = selectResult.getHead();
        Table table = new Table(head, data);
        table.print();
    }

    public static class Table {
        final String[][] table;
        /**
         * 每行最长
         **/
        final int[] maxLength;
        /**
         * 一共多长
         **/
        final int rowLength;
        /**
         * 分隔符的位置
         **/
        final List<Integer> splitIndex;

        char[] splitLine;

        public Table(List<String> head, List<Map<String, String>> data) {
            this.table = new String[data.size() + 1][head.size()];
            this.maxLength = new int[head.size()];
            for (int i = 0; i < head.size(); i++) {
                String headCellValue = head.get(i);
                maxLength[i] = length(headCellValue);
                table[0][i] = head.get(i);
            }
            for (int i = 1; i < this.table.length; i++) {
                for (int row = 0; row < head.size(); row++) {
                    String cellValue = data.get(i - 1).get(head.get(row));
                    table[i][row] = cellValue;
                    maxLength[row] = Math.max(maxLength[row], length(cellValue));
                }
            }
            int rowLength = 2;
            for (int i = 0; i < maxLength.length; i++) {
                maxLength[i] += 4;
                rowLength += maxLength[i];
            }
            rowLength += 1;
            this.rowLength = rowLength;
            this.splitIndex = new ArrayList<>();
            int fillIndex = 0;
            splitIndex.add(fillIndex);
            for (int i : maxLength) {
                fillIndex += i;
                splitIndex.add(fillIndex);
            }
            splitIndex.set(splitIndex.size() - 1, rowLength);
        }


        public void print() {
            System.out.println(splitLine());
            System.out.println(dataLine(this.table[0]));
            for (int i = 1; i < this.table.length; i++) {
                System.out.println(splitLine());
                System.out.println(dataLine(this.table[i]));
            }
            System.out.println(splitLine());
        }


        private char[] splitLine() {
            if (splitLine != null) {
                return splitLine;
            }
            splitLine = emptyLine('-');
            for (Integer index : splitIndex) {
                splitLine[index] = '+';
            }
            return splitLine;
        }

        private char[] emptyLine(char sep) {
            char[] emptyLine = new char[rowLength + 1];
            Arrays.fill(emptyLine, sep);
            return emptyLine;
        }

        private char[] dataLine(String[] data) {
            char[] headLine = emptyLine(' ');
            int skipLength = 0;
            for (int i = 0; i < data.length; i++) {
                String headCell = data[i];
                //  最左边一个  + 空一个 + 前面所有的
                int startIndex = 2 + skipLength;
                skipLength += maxLength[i];
                for (int charIndex = 0; charIndex < headCell.toCharArray().length; charIndex++) {
                    headLine[startIndex + charIndex] = headCell.charAt(charIndex);
                }
            }
            for (Integer index : splitIndex) {
                headLine[index] = '|';
            }
            return headLine;
        }


        private int length(String str) {
            if (CHINESE_CHARACTERS_PATTERN.matcher(str).find()) {
                return str.length() + 4;
            }
            return str.length();
        }


    }


}
