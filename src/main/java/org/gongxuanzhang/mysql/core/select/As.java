package org.gongxuanzhang.mysql.core.select;

import lombok.Data;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author gongxuanzhang
 */
@Data
public class As implements Iterable<SelectCol> {

    private List<SelectCol> selectColList = new ArrayList<>();


    public void addSelectCol(SelectCol selectCol) {
        this.selectColList.add(selectCol);
    }

    public boolean isEmpty() {
        return selectColList.isEmpty();
    }

    @Override
    public Iterator<SelectCol> iterator() {
        return selectColList.iterator();
    }
}
