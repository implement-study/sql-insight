package org.gongxuanzhang.mysql.core.convert;

import org.springframework.util.StringUtils;

/**
 * @author gxz gongxuanzhang@foxmail.com
 **/
public class TimeStampConverter implements Converter<Long> {

    @Override
    public Long convert(String value) {
        if (StringUtils.hasText(value)) {
            return Long.valueOf(value);
        }
        return null;
    }
}
