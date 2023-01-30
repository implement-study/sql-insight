package org.gongxuanzhang.mysql.core.select.condition;

import org.gongxuanzhang.mysql.core.Expression;

import java.util.function.IntFunction;

/**
 * @author gxz gongxuanzhang@foxmail.com
 **/
public class IntegerCondition implements Expression<Integer> {

    private final IntFunction<Integer> intFunction;

    private final int value;

    public IntegerCondition(int value, IntFunction<Integer> intFunction) {
        this.intFunction = intFunction;
        this.value = value;
    }

    public IntegerCondition(int value) {
        this.intFunction = null;
        this.value = value;
    }

    @Override
    public Integer getValue() {
        if (intFunction == null) {
            return value;
        }
        return intFunction.apply(value);
    }
}
