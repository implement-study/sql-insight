package org.gongxuanzhang.mysql.core.convert;

/**
 * 转换器，负责转换字符和目标数据
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
public interface Converter<T> {


    /**
     * 表示的字符串 转换为目标对象
     *
     * @param value 字符串
     * @return 可以为空
     **/
    T convert(String value);


}
