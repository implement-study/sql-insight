package org.gongxuanzhang.mysql.entity;

import lombok.Data;
import org.gongxuanzhang.mysql.core.TableInfoBox;
import org.gongxuanzhang.mysql.core.WhereBox;
import org.gongxuanzhang.mysql.core.select.Where;

/**
 * update info
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
@Data
public class UpdateInfo implements ExecuteInfo, TableInfoBox, WhereBox {

    private TableInfo tableInfo;

    /**
     * set内容
     **/
    private SetInfo set = new SetInfo();

    private Where where;

}
