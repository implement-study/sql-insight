package org.gongxuanzhang.mysql.core.select;

import lombok.Data;
import org.gongxuanzhang.mysql.core.TableInfoBox;
import org.gongxuanzhang.mysql.entity.TableInfo;

/**
 * select 中的from内容
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
@Data
public class From implements TableInfoBox {


    /**
     * 查询主表
     **/
    private TableInfo main;

    @Override
    public TableInfo getTableInfo() {
        return this.main;
    }
}
