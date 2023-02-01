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
