package org.gongxuanzhang.sql.insight.core.engine.storage.innodb.index;


import org.gongxuanzhang.sql.insight.core.environment.SessionContext;
import org.gongxuanzhang.sql.insight.core.object.Column;
import org.gongxuanzhang.sql.insight.core.object.Cursor;
import org.gongxuanzhang.sql.insight.core.object.InsertRow;
import org.gongxuanzhang.sql.insight.core.object.Table;

import java.io.File;
import java.util.List;

/**
 * second index
 *
 * @author gongxuanzhangmelt@gmail.com
 **/
public class SecondIndex extends InnodbIndex {


    protected SecondIndex(Table table) {
        super(table);
    }

    @Override
    public void rndInit() {

    }

    @Override
    public int getId() {
        return 0;
    }

    @Override
    public Table belongTo() {
        return null;
    }

    @Override
    public Cursor find(SessionContext sessionContext) {
        return null;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public void insert(InsertRow row) {

    }

    @Override
    public File getFile() {
        return null;
    }

    @Override
    public List<Column> columns() {
        return null;
    }

}
