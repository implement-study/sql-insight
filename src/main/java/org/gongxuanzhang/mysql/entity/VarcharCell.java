package org.gongxuanzhang.mysql.entity;

import lombok.Data;

/**
 * var char单元格
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
@Data
public class VarcharCell implements Cell<String> {

    private final ColumnType type = ColumnType.STRING;

    private final String value;

}
