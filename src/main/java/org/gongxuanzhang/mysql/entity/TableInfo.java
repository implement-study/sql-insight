package org.gongxuanzhang.mysql.entity;

import lombok.Data;
import org.gongxuanzhang.mysql.annotation.DependOnContext;
import org.gongxuanzhang.mysql.core.EngineSelectable;
import org.gongxuanzhang.mysql.core.SessionManager;
import org.gongxuanzhang.mysql.exception.ExecuteException;
import org.gongxuanzhang.mysql.exception.MySQLException;
import org.gongxuanzhang.mysql.tool.Context;

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
@DependOnContext
public class TableInfo implements ExecuteInfo, EngineSelectable {


    /**
     * 在mysql的frm文件上加了个我的姓 嘿嘿
     **/
    public final static String GFRM_SUFFIX = ".gfrm";

    public final static String GIBD_SUFFIX = ".gibd";

    private DatabaseInfo database;

    private String tableName;
    private List<ColumnInfo> columnInfos;
    private List<String> primaryKey;
    private String comment;
    private String engineName;


    /**
     * 表结构文件
     **/
    @DependOnContext
    public File structFile() throws MySQLException {
        File databaseDir = checkDatabase();
        return new File(databaseDir, this.tableName + GFRM_SUFFIX);
    }

    /**
     * 表数据文件
     **/
    @DependOnContext
    public File dataFile() throws MySQLException {
        File databaseDir = checkDatabase();
        return new File(databaseDir, this.tableName + GIBD_SUFFIX);
    }

    /**
     * 校验数据库信息
     *
     * @return 返回数据库文件夹
     * @throws MySQLException 校验失败会抛出异常
     **/
    private File checkDatabase() throws MySQLException {
        if (database == null) {
            String sessionDb = SessionManager.currentSession().getDatabase();
            this.database = Context.getDatabaseManager().select(sessionDb);
        }
        if (database == null) {
            throw new MySQLException("无法获取database");
        }
        File databaseDir = this.database.sourceFile();
        if (!databaseDir.exists() || !databaseDir.isDirectory()) {
            throw new ExecuteException("数据库[" + database + "]不存在");
        }
        return databaseDir;
    }

    /**
     * 返回带数据库的完整表名
     */
    public String absoluteName() {
        return database.getDatabaseName() + "." + this.tableName;
    }


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
