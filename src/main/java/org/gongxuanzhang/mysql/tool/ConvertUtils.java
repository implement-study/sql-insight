package org.gongxuanzhang.mysql.tool;

import org.gongxuanzhang.mysql.core.convert.Converter;
import org.gongxuanzhang.mysql.core.convert.IntConverter;
import org.gongxuanzhang.mysql.core.convert.TimeStampConverter;
import org.gongxuanzhang.mysql.core.convert.VarcharConverter;
import org.gongxuanzhang.mysql.entity.ColumnType;
import org.gongxuanzhang.mysql.exception.MySQLException;

import java.util.HashMap;
import java.util.Map;

/**
 * convert 工具类
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
public class ConvertUtils {

    private ConvertUtils() {

    }

    private static final Map<ColumnType, Converter<?>> CONVERTER_MAP = new HashMap<>();

    static {
        CONVERTER_MAP.put(ColumnType.INT, new IntConverter());
        CONVERTER_MAP.put(ColumnType.STRING, new VarcharConverter());
        CONVERTER_MAP.put(ColumnType.TIMESTAMP, new TimeStampConverter());

    }


    public static <T> T convert(ColumnType type, String value) throws MySQLException {
        Converter<?> converter = CONVERTER_MAP.get(type);
        if (converter == null) {
            String message = "类型[%s]值[%s]无法转换";
            throw new MySQLException(String.format(message, type, value));
        }
        return (T) converter.convert(value);
    }
}
