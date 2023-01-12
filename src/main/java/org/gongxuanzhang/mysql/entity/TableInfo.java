package org.gongxuanzhang.mysql.entity;

import lombok.Data;
import org.gongxuanzhang.mysql.core.EngineSelectable;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 表信息
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
@Data
public class TableInfo implements ExecuteInfo, EngineSelectable {

    private String database;
    private String tableName;
    private List<ColumnInfo> columnInfos;
    private List<String> primaryKey;
    private String comment;
    private String engineName;
    /**
     * 表结构文件
     **/
    private File file;


    public List<Map<String, String>> descTable() {
        List<Map<String, String>> result = new ArrayList<>();
        Set<String> primary = primaryKey == null ? new HashSet<>() : new HashSet<>(primaryKey);
        for (ColumnInfo columnInfo : columnInfos) {
            Map<String, String> colInfo = new HashMap<>(8);
            colInfo.put("field", columnInfo.getName());
            colInfo.put("type", columnInfo.getType().keyword);
            colInfo.put("null", Boolean.toString(!columnInfo.isNotNull()));
            colInfo.put("primary key", Boolean.toString(primary.contains(columnInfo.getName())));
            colInfo.put("default", columnInfo.getDefaultValue());
            colInfo.put("auto_increment", Boolean.toString(columnInfo.isAutoIncrement()));
            result.add(colInfo);
        }
        return result;
    }


    public void addPrimaryKey(String primaryKey) {
        if (this.primaryKey == null) {
            this.primaryKey = new ArrayList<>();
        }
        this.primaryKey.add(primaryKey);
    }

}
