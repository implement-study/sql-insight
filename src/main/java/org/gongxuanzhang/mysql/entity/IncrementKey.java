package org.gongxuanzhang.mysql.entity;

import lombok.Data;
import org.gongxuanzhang.mysql.annotation.DependOnContext;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author gongxuanzhang
 */
@Data
@DependOnContext
public class IncrementKey implements Serializable {
    private final String colName;
    private AtomicInteger incrementValue = new AtomicInteger(0);

    public IncrementKey(String colName) {
        this.colName = colName;
    }

    /**
     * 拿到自增主键的值
     *
     * @return 拿到下一个应该获取的主键值
     */

    public int nextKey() {
        return incrementValue.incrementAndGet();
    }

    public void check(int value) {
        if (incrementValue.get() < value) {
            incrementValue.set(value);
        }
    }

}
