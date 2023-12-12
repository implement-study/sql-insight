package org.gongxuanzhang.sql.insight.core.object;


/**
 * primary key index
 *
 * @author gongxuanzhangmelt@gmail.com
 **/
public abstract class PKIndex implements Index {


    @Override
    public int getId() {
        return 1;
    }
}
