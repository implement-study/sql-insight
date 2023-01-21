package org.gongxuanzhang.mysql.service.executor.session.show;

import org.gongxuanzhang.mysql.core.result.Result;
import org.gongxuanzhang.mysql.exception.MySQLException;
import org.gongxuanzhang.mysql.service.executor.Executor;

/**
 * 显示内容
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
public interface Shower extends Executor {


    /**
     * 展示
     *
     * @return 同 executor
     * @throws MySQLException 同executor
     **/
    Result show() throws MySQLException;

    /**
     * 委托为show
     *
     * @return 同executor
     * @throws MySQLException 同executor
     **/
    @Override
    default Result doExecute() throws MySQLException {
        return show();
    }
}
