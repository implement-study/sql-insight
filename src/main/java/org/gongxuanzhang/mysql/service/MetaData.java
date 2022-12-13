package org.gongxuanzhang.mysql.service;

import lombok.Data;

import java.util.Map;

/**
 * 返回值的元数据
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
@Data
public class MetaData {

    Map<String, String> map;
}
