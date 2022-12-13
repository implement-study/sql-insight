package org.gongxuanzhang.mysql.service;


/**
 * service返回的统一实体
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
public interface Result {

    /**
     * 返回码
     **/
    int getCode();

    /**
     * 错误信息,如果正常返回没有此属性
     **/
    String getErrorMessage();


}
