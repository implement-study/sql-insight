package org.gongxuanzhang.mysql.core.result;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 携带信息的返回值
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
@EqualsAndHashCode(callSuper = true)
@Data
public class InfoResult extends SuccessResult {

    private String message;

    public InfoResult(String message) {
        this.message = message;
    }

    @Override
    public int getCode() {
        return 200;
    }

}
