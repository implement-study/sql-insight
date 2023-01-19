package org.gongxuanzhang.mysql.core.convert;

import org.springframework.util.StringUtils;

/**
 * @author gxz gongxuanzhang@foxmail.com
 **/
public class IntConverter implements Converter<Integer> {

    @Override
    public Integer convert(String value) {
        if (StringUtils.hasText(value)) {
            return Integer.valueOf(value);
        }
        return null;
    }
}
