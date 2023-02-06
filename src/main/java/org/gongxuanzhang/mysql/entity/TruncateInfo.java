package org.gongxuanzhang.mysql.entity;

import lombok.Data;
import org.gongxuanzhang.mysql.core.TableInfoBox;

/**
 * truncate table
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
@Data
public class TruncateInfo implements ExecuteInfo, TableInfoBox {

    private TableInfo tableInfo;

}
