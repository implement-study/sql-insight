package org.gongxuanzhang.mysql.storage.fool;

import com.alibaba.fastjson2.JSONObject;
import org.gongxuanzhang.mysql.core.result.Result;
import org.gongxuanzhang.mysql.entity.TableInfo;
import org.gongxuanzhang.mysql.entity.UpdateInfo;
import org.gongxuanzhang.mysql.exception.MySQLException;
import org.gongxuanzhang.mysql.storage.UpdateEngine;
import org.gongxuanzhang.mysql.tool.Context;
import org.gongxuanzhang.mysql.tool.ExceptionThrower;
import org.gongxuanzhang.mysql.tool.FileUtils;
import org.gongxuanzhang.mysql.tool.Pair;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

/**
 * fool 引擎的修改方法
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
public class FoolUpdate implements UpdateEngine {
    @Override
    public Result update(UpdateInfo info) throws MySQLException {
        TableInfo tableInfo = info.getTableInfo();
        File dataFile = Context.getTableManager().select(tableInfo).dataFile();
        List<String> writeCandidate = new ArrayList<>();
        AtomicInteger updateCount = new AtomicInteger(0);
        FileUtils.readAllLines(dataFile.toPath(), (line) -> {
            JSONObject jsonObject = JSONObject.parseObject(line);
            if (info.getWhere().hit(jsonObject)) {
                updateCount.incrementAndGet();
                for (Pair<String, Function<String, ?>> setItem : info.getSet()) {
                    String colName = setItem.getKey();
                    Function<String, ?> function = setItem.getValue();
                    Object newValue = function.apply(jsonObject.get(colName).toString());
                    jsonObject.put(colName, newValue);
                }
            }
            writeCandidate.add(jsonObject.toString());
        });
        try {
            Files.write(dataFile.toPath(), writeCandidate);
        } catch (IOException e) {
            ExceptionThrower.errorSwap(e);
        }
        return Result.info(String.format("成功修改%s条记录", updateCount.get()));

    }
}
