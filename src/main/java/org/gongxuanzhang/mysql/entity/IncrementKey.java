package org.gongxuanzhang.mysql.entity;

import lombok.Data;
import org.gongxuanzhang.mysql.annotation.DependOnContext;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author gongxuanzhang
 */
@Data
@DependOnContext
public class IncrementKey {
    private String colName;
    private AtomicInteger incrementValue = new AtomicInteger(0);


    /**
     * 拿到自增主键的值
     * @return 拿到下一个应该获取的主键值
     */

    public int nextKey() {
        return incrementValue.incrementAndGet();
    }

}