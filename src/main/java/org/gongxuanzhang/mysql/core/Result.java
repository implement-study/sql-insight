package org.gongxuanzhang.mysql.core;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * service返回的统一实体
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
public interface Result {

    /**
     * 返回码
     *
     * @return 如果正常返回  是200 非正常返回统一 是非200 不做区分
     **/
    int getCode();

    /**
     * 错误信息,如果正常返回没有此属性
     *
     * @return 错误信息  如果没有错误信息 是null
     **/
    String getErrorMessage();


    /**
     * 返回简单成功
     *
     * @return 成功且没有信息
     **/
    static Result success() {
        return new SuccessResult();
    }

    /**
     * 错误返回
     *
     * @param errorMessage 错误信息
     * @return 错误返回
     **/
    static Result error(String errorMessage) {
        return new ErrorResult(errorMessage);
    }

    /**
     * 返回有查询结果
     *
     * @param head     元数据头
     * @param dataList 元数据
     * @return 结果
     **/
    static Result select(String[] head, List<Map<String, String>> dataList) {
        return new SelectResult(head, dataList);
    }

    /**
     * 返回单列结果
     *
     * @param head     列标题
     * @param dataList 列数据
     * @return 结果
     **/
    static Result singleRow(String head, List<String> dataList) {
        if (dataList == null) {
            dataList = new ArrayList<>();
        }
        return new SingleRowResult(head, dataList);
    }


}
