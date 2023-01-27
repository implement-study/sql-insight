package org.gongxuanzhang.mysql.core.select;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author gongxuanzhang
 */
@Data
public class As {

    private List<SelectCol> selectColList = new ArrayList<>();


    public void addSelectCol(SelectCol selectCol) {
        this.selectColList.add(selectCol);
    }

}
