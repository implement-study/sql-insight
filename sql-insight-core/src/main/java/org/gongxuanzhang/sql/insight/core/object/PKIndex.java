package org.gongxuanzhang.sql.insight.core.object;


/**
 * primary key index
 *
 * @author gongxuanzhangmelt@gmail.com
 **/
public abstract class PKIndex implements Index {

    Table table;

    protected PKIndex(Table table) {
        this.table = table;
    }

    @Override
    public Table getTable() {
        return table;
    }

    @Override
    public void setTable(Table table) {
        this.table = table;
    }
}
