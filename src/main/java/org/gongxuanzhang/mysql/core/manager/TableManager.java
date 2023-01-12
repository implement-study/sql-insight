package org.gongxuanzhang.mysql.core.manager;

import lombok.extern.slf4j.Slf4j;
import org.gongxuanzhang.mysql.entity.DatabaseInfo;
import org.gongxuanzhang.mysql.entity.TableInfo;
import org.gongxuanzhang.mysql.exception.MySQLException;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;

/**
 * 表管理
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
@Slf4j
public class TableManager extends AbstractManager<TableInfo> {

    /**
     * 在mysql的frm文件上加了个我的姓 嘿嘿
     **/
    private final static String GFRM_SUFFIX = ".gfrm";

    private final DatabaseManager databaseManager;

    public TableManager(DatabaseManager databaseManager) throws MySQLException {
        this.databaseManager = databaseManager;
    }


    @Override
    protected void init() throws MySQLException {
        databaseManager.getAll().stream()
                .map(DatabaseInfo::getDatabaseDir)
                .filter((file) -> file.getName().endsWith(GFRM_SUFFIX))
                .map(this::gfrmToInfo).forEach(this::register);
    }

    private TableInfo gfrmToInfo(File gfrmFile) {
        try (FileInputStream fileInputStream = new FileInputStream(gfrmFile);
             ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream)) {
            return (TableInfo) objectInputStream.readObject();
        } catch (Exception e) {
            throw new RuntimeException(gfrmFile.getName() + "表文件有问题，无法启动mysql,错误信息:" + e.getMessage());
        }

    }

    @Override
    public String toId(TableInfo info) {
        return info.getDatabase() + "." + info.getTableName();
    }
}