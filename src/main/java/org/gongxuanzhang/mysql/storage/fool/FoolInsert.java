package org.gongxuanzhang.mysql.storage.fool;

import com.alibaba.fastjson2.JSONObject;
import org.gongxuanzhang.mysql.core.ColumnAdjust;
import org.gongxuanzhang.mysql.core.result.Result;
import org.gongxuanzhang.mysql.entity.InsertData;
import org.gongxuanzhang.mysql.entity.InsertInfo;
import org.gongxuanzhang.mysql.entity.TableInfo;
import org.gongxuanzhang.mysql.exception.MySQLException;
import org.gongxuanzhang.mysql.storage.InsertEngine;
import org.gongxuanzhang.mysql.tool.FileUtils;

import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * fool 引擎的插入模板
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
public class FoolInsert implements InsertEngine {

    @Override
    public Result insert(InsertInfo info) throws MySQLException {
        ColumnAdjust columnAdjust = new ColumnAdjust(info);
        InsertData insertData = columnAdjust.adjust();
        TableInfo tableInfo = info.getTableInfo();
        Set<String> uniqueKeys = tableInfo.uniqueKeys();
        if (uniqueKeys.isEmpty()) {
            return withoutUnique(insertData, tableInfo.dataFile().toPath());
        }
        return uniqueData(insertData, tableInfo);
    }


    private Result uniqueData(InsertData insertData, TableInfo tableInfo) throws MySQLException {
        Set<String> uniqueKeys = tableInfo.uniqueKeys();
        FileUtils.readAllLines(tableInfo.dataFile().toPath(), (line) -> {
            JSONObject jsonObject = JSONObject.parseObject(line);
            for (String uniqueKey : uniqueKeys) {
                Object uniqueValue = jsonObject.get(uniqueKey);
                if (uniqueValue != null && insertData.containsUnique(uniqueKey, uniqueValue)) {
                    throw new MySQLException(String.format("键[%s],值[%s]已经存在", uniqueKey, uniqueValue));
                }
            }
        });
        FileUtils.append(tableInfo.dataFile().toPath(), insertData.getInsertStr());
        return Result.info(String.format("成功插入%s条数据", insertData.getInsertStr().size()));
    }

    private Result withoutUnique(InsertData insertData, Path dataPath) throws MySQLException {
        List<String> insertStr = insertData.getData().stream().map(JSONObject::toString).collect(Collectors.toList());
        FileUtils.append(dataPath, insertStr);
        return Result.info(String.format("成功插入%s条数据", insertStr.size()));
    }
}
