package org.gongxuanzhang.mysql.core;

/**
 * 可选择引擎
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
public interface EngineSelectable {


    /**
     * 引擎名称
     *
     * @return 不能返回null
     **/
    String getEngineName();
}
