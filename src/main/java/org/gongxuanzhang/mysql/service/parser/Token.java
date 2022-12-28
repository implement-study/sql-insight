package org.gongxuanzhang.mysql.service.parser;

import java.util.Set;

/**
 * 词法token
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
public interface Token {


    /**
     * 终止符
     *
     * @return true是 终止符  不需要往后判断
     **/
    boolean terminate();


    /**
     * 允许的下一个token位置
     *
     * @return 如果是终止符  返回null 或者空集合
     **/
    Set<Class<? extends Token>> allowNext();


}
