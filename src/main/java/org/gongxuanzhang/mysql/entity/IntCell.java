package org.gongxuanzhang.mysql.entity;

import lombok.Data;

/**
 * int单元格
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
@Data
public class IntCell implements Cell<Integer> {

    private final ColumnType type = ColumnType.INT;

    private final Integer value;

}
