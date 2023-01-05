package org.gongxuanzhang.mysql.service.executor.session.show;

import org.gongxuanzhang.mysql.core.Result;
import org.gongxuanzhang.mysql.service.executor.Executor;

/**
 * 显示内容
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
public interface Shower extends Executor {


    /**
     * 展示
     **/
    Result show();

    @Override
    default Result doExecute() {
        return show();
    }
}
