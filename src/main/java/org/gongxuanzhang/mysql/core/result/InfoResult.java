package org.gongxuanzhang.mysql.core.result;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

/**
 * 携带信息的返回值
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
@EqualsAndHashCode(callSuper = true)
@Data
@Slf4j
public class InfoResult extends SuccessResult {

    private String message;

    public InfoResult(String message) {
        log.info(message);
        this.message = message;
    }

    @Override
    public int getCode() {
        return 200;
    }

}
