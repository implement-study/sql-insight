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

    @DependOnContext
    public static void readAllLines(Path path, LineHandle handle) throws MySQLException {

        try (BufferedReader reader = Files.newBufferedReader(path)) {
            String line;
            while ((line = reader.readLine()) != null) {
                handle.handle(line);
            }
        } catch (IOException e) {
            ExceptionThrower.errorSwap(e);
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
