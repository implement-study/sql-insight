package org.gongxuanzhang.mysql.tool;

import org.gongxuanzhang.mysql.core.ErrorResult;
import org.gongxuanzhang.mysql.core.result.Result;
import org.gongxuanzhang.mysql.core.result.SelectResult;
import org.gongxuanzhang.mysql.core.result.SuccessResult;

import java.util.List;
import java.util.Map;

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
        List<Map<String, ?>> data = selectResult.getData();
        for (Map<String, ?> datum : data) {
            System.out.println(datum);
        }
    }

}
