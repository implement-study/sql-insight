package org.gongxuanzhang.mysql.entity;

import lombok.Data;
import org.gongxuanzhang.mysql.core.TableInfoBox;

import java.util.ArrayList;
import java.util.List;


/**
 * insert into info
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
@Data
public class InsertInfo implements ExecuteInfo, TableInfoBox {

    private TableInfo tableInfo = new TableInfo();

    /**
     * 需要插入的列名
     **/
    private List<String> columns = new ArrayList<>();

    private List<List<Cell<?>>> insertData;


}
