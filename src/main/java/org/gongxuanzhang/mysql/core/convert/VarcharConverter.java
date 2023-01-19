package org.gongxuanzhang.mysql.core.convert;


/**
 * @author gxz gongxuanzhang@foxmail.com
 **/
public class VarcharConverter implements Converter<String> {

    @Override
    public String convert(String value) {
        return value;
    }
}
