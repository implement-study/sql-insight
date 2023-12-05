package org.gongxuanzhang.sql.insight.core.object;


/**
 * primary key index
 *
 * @author gongxuanzhangmelt@gmail.com
 **/
public class PKIndex implements Index {

    Table table;

    @Override
    public Row nextRow() {
        return null;
    }
}
