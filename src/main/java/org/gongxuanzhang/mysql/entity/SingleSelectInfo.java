package org.gongxuanzhang.mysql.entity;

import lombok.Data;
import org.gongxuanzhang.mysql.core.FromBox;
import org.gongxuanzhang.mysql.core.OrderBox;
import org.gongxuanzhang.mysql.core.WhereBox;
import org.gongxuanzhang.mysql.core.select.As;
import org.gongxuanzhang.mysql.core.select.From;
import org.gongxuanzhang.mysql.core.select.Order;
import org.gongxuanzhang.mysql.core.select.Where;

/**
 * 单表查询信息
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
@Data
public class SingleSelectInfo implements ExecuteInfo, FromBox, WhereBox, OrderBox {


    private From from;

    private As as;

    private Where where;

    private Order<?> order;


}
