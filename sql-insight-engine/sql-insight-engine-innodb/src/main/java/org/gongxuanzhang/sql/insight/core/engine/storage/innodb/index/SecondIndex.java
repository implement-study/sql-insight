package org.gongxuanzhang.sql.insight.core.engine.storage.innodb.index;


import org.gongxuanzhang.sql.insight.core.environment.SessionContext;
import org.gongxuanzhang.sql.insight.core.object.Cursor;
import org.gongxuanzhang.sql.insight.core.object.Index;
import org.gongxuanzhang.sql.insight.core.object.InsertRow;
import org.gongxuanzhang.sql.insight.core.object.Table;

/**
 * second index
 *
 * @author gongxuanzhangmelt@gmail.com
 **/
public class SecondIndex implements Index {


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
    public void insert(InsertRow row) {

    }

}
