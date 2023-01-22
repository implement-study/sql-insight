package org.gongxuanzhang.mysql.service.executor;

import org.gongxuanzhang.mysql.core.result.Result;
import org.gongxuanzhang.mysql.core.result.SelectResult;
import org.gongxuanzhang.mysql.core.select.Where;
import org.gongxuanzhang.mysql.entity.DatabaseInfo;
import org.gongxuanzhang.mysql.entity.TableInfo;

import java.util.List;
import java.util.Map;

/**
 * 查询执行器
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
public class SelectExecutor implements Executor {


    /**
     * 查询的数据库信息
     **/
    private DatabaseInfo databaseInfo;

    /**
     * 要查询的表信息
     **/
    private TableInfo tableInfo;

    /**
     * 查询的where条件
     **/
    private Where where;

    /**
     * 查询目标字段
     **/
    private List<String> targetColumn;

    /**
     * 别名
     **/
    private Map<String, String> alias;

    @Override
    public Result doExecute() {
        SelectResult result = new SelectResult(null, null);
//        List<MetaData> list = new ArrayList<>();
//        for (int i = 0; i < 10; i++) {
//            Map<String, String> stringStringMap = new HashMap<>();
//            stringStringMap.put("id", i + "");
//            MetaData metaData = new MetaData();
//            metaData.setMap(stringStringMap);
//            list.add(metaData);
//        }
//        result.setData(list);
        return result;
    }
}
