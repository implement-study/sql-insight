package org.gongxuanzhang.mysql.service.executor.session;

import org.gongxuanzhang.mysql.core.MySqlSession;
import org.gongxuanzhang.mysql.core.Result;
import org.gongxuanzhang.mysql.core.SessionManager;
import org.gongxuanzhang.mysql.entity.GlobalProperties;
import org.gongxuanzhang.mysql.entity.VariableInfo;
import org.gongxuanzhang.mysql.exception.MySQLException;
import org.gongxuanzhang.mysql.service.executor.Executor;

import static org.gongxuanzhang.mysql.tool.ExceptionThrower.errorSwap;

/**
 * set执行器
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
public class SetExecutor implements Executor {

    private final VariableInfo varInfo;

    public SetExecutor(VariableInfo setInfo) {
        this.varInfo = setInfo;
    }

    @Override
    public Result doExecute() throws MySQLException {
        try {
            if (varInfo.isGlobal()) {
                GlobalProperties.getInstance().set(varInfo.getName(), varInfo.getValue());
            } else {
                MySqlSession mySqlSession = SessionManager.currentSession();
                mySqlSession.set(varInfo.getName(), varInfo.getValue());
            }
            return Result.success();
        } catch (Exception e) {
            return errorSwap(e);
        }
    }
}
