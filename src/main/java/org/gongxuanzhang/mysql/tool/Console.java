package org.gongxuanzhang.mysql.tool;

import com.alibaba.fastjson2.JSONObject;
import org.gongxuanzhang.mysql.core.ErrorResult;
import org.gongxuanzhang.mysql.core.result.Result;
import org.gongxuanzhang.mysql.core.result.SelectResult;
import org.gongxuanzhang.mysql.core.result.SuccessResult;

import java.util.List;

/**
 * @author gxz gongxuanzhang@foxmail.com
 **/
public class Console {

    public static void infoResult(Result result) {
        if (result instanceof SelectResult) {
            selectInfo((SelectResult) result);
        } else if (result instanceof ErrorResult) {
            System.err.println(result.getErrorMessage());
        } else if (result instanceof SuccessResult) {
            System.out.println("成功执行");
        }

    }

    private static void selectInfo(SelectResult selectResult) {
        List<JSONObject> data = selectResult.getData();
        data.forEach(System.out::println);
    }

}
