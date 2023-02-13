package org.gongxuanzhang.mysql.entity;

import org.gongxuanzhang.mysql.tool.Pair;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * set info
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
public class SetInfo implements ExecuteInfo, Iterable<Pair<String, String>> {

    //  todo 暂时写成 String  以后要变成cell

    private final List<Pair<String, String>> setList = new ArrayList<>();


    public void addSet(String colName, String value) {
        setList.add(Pair.of(colName,value));
    }


    @Override
    public Iterator<Pair<String, String>> iterator() {
        return setList.iterator();
    }
}
