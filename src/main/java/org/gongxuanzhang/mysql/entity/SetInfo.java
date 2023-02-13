package org.gongxuanzhang.mysql.entity;

import org.gongxuanzhang.mysql.tool.Pair;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;

/**
 * set info
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
public class SetInfo implements ExecuteInfo, Iterable<Pair<String, Function<String, ?>>> {

    //  todo 暂时写成 String  以后要变成cell

    private final List<Pair<String, Function<String, ?>>> setList = new ArrayList<>();


    public void addSet(String colName, String value) {
        setList.add(Pair.of(colName, (arbitrary) -> value));
    }

    public void addSet(String colName, Function<String, ?> function) {
        setList.add(Pair.of(colName, function));
    }


    @Override
    public Iterator<Pair<String, Function<String, ?>>> iterator() {
        return setList.iterator();
    }
}
