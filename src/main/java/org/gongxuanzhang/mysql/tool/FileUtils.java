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

import org.gongxuanzhang.mysql.annotation.DependOnContext;
import org.gongxuanzhang.mysql.exception.MySQLException;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

/**
 * @author gxz gongxuanzhang@foxmail.com
 **/
public class FileUtils {
    private FileUtils() {

    }

    /**
     * 按行读取文件
     *
     * @param path   path
     * @param handle 处理器
     *
     * @return 总行数
     **/
    @DependOnContext
    public static int readAllLines(Path path, LineHandle handle) throws MySQLException {
        int lineSize = 0;
        try (BufferedReader reader = Files.newBufferedReader(path)) {
            String line;
            while ((line = reader.readLine()) != null) {
                lineSize++;
                handle.handle(line);
            }
            return lineSize;
        } catch (IOException e) {
            return ExceptionThrower.errorSwap(e);
        }

    }

    public static void append(Path path, Iterable<String> iterable) throws MySQLException {
        try {
            Files.write(path, iterable, StandardOpenOption.APPEND);
        } catch (IOException e) {
            ExceptionThrower.errorSwap(e);
        }

    }
}
