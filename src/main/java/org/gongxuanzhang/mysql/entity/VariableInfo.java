package org.gongxuanzhang.mysql.entity;

import lombok.Data;

/**
 * 变量修改信息
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
@Data
public class VariableInfo implements ExecuteInfo {

    private String name;
    private String value;
    private boolean global;

}
