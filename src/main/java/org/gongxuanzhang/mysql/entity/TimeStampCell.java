package org.gongxuanzhang.mysql.entity;

import lombok.Data;

/**
 * time stamp 单元格
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
@Data
public class TimeStampCell implements Cell<Long> {

    private final ColumnType type = ColumnType.TIMESTAMP;

    private final Long value;

}
