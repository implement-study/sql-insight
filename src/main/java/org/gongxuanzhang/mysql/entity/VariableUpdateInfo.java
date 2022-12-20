package org.gongxuanzhang.mysql.entity;

import lombok.Data;

import java.util.List;

/**
 * 变量修改信息
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
@Data
public class VariableUpdateInfo implements ExecuteInfo {

    private List<VariableInfo> variableInfos;

}
