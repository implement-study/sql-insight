package org.gongxuanzhang.mysql.entity;

import lombok.Data;
import org.gongxuanzhang.mysql.core.select.As;
import org.gongxuanzhang.mysql.core.select.Where;

/**
 * 单表查询信息
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
@Data
public class SingleSelectInfo implements ExecuteInfo {


    /**
     * 查询的主表
     */
    private TableInfo mainTable;

    private As as;

    private Where where;




}
