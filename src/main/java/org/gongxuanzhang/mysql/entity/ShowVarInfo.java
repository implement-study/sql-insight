package org.gongxuanzhang.mysql.entity;

import lombok.Data;

/**
 * show var info
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
@Data
public class ShowVarInfo implements ExecuteInfo {

   private boolean global;
   private boolean session;

}
