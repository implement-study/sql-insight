package org.gongxuanzhang.mysql.storage.fool;

import com.alibaba.fastjson2.JSONObject;
import org.gongxuanzhang.mysql.core.result.Result;
import org.gongxuanzhang.mysql.core.select.Order;
import org.gongxuanzhang.mysql.core.select.SelectCol;
import org.gongxuanzhang.mysql.core.select.Where;
import org.gongxuanzhang.mysql.entity.SingleSelectInfo;
import org.gongxuanzhang.mysql.entity.TableInfo;
import org.gongxuanzhang.mysql.exception.MySQLException;
import org.gongxuanzhang.mysql.storage.SelectEngine;
import org.gongxuanzhang.mysql.tool.FileUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * fool 的 查询引擎
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
public class FoolSelect implements SelectEngine {


    @Override
    public Result select(SingleSelectInfo info) throws MySQLException {
        List<Map<String, ?>> data = new ArrayList<>();
        TableInfo tableInfo = info.getFrom().getTableInfo();
        Where where = info.getWhere();
        List<SelectCol> selectCols = tableInfo.scatterCol(info.getAs());
        List<JSONObject> viewJson = new ArrayList<>();

        FileUtils.readAllLines(tableInfo.dataFile().toPath(), (line) -> {
            JSONObject jsonObject = JSONObject.parseObject(line);
            // where
            if (where.available() && where.hit(jsonObject)) {
                viewJson.add(jsonObject);
            }
        });
        //  as
        viewJson.forEach((json) -> {
            for (SelectCol selectCol : selectCols) {
                json.put(selectCol.getAlias(), json.get(selectCol.getColName()));
            }
        });
        // order
        Order<?> order = info.getOrder();
        if (order.available()) {
            viewJson.sort((Order<JSONObject>) order);
        }
        List<String> colNames = new ArrayList<>();
        for (SelectCol selectCol : selectCols) {
            colNames.add(selectCol.getAlias());
        }
        return Result.select(colNames.toArray(new String[]{}), data);
    }
}
