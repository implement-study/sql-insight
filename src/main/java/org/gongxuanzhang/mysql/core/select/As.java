package org.gongxuanzhang.mysql.core.select;

import lombok.Data;
import org.gongxuanzhang.mysql.exception.MySQLException;

import java.util.HashMap;
import java.util.Map;

/**
 * @author gongxuanzhang
 */
@Data
public class As {

    private boolean all;
    private Map<String, String> as = new HashMap<>();

    public void addAlias(String colName, String alias) throws MySQLException {
        if (as.containsKey(colName)) {
            throw new MySQLException(colName + "已经有别名");
        }
        as.put(colName, alias);
    }


}
