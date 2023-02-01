package org.gongxuanzhang.mysql.entity;

import lombok.Data;
import org.gongxuanzhang.mysql.core.FromBox;
import org.gongxuanzhang.mysql.core.WhereBox;
import org.gongxuanzhang.mysql.core.select.From;
import org.gongxuanzhang.mysql.core.select.Where;

/**
 * 删除信息
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
@Data
public class DeleteInfo implements ExecuteInfo, FromBox, WhereBox {

    private From from;

    private Where where;


}
