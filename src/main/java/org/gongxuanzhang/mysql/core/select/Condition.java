package org.gongxuanzhang.mysql.core.select;


import com.alibaba.fastjson2.JSONObject;
import org.gongxuanzhang.mysql.exception.MySQLException;
import org.gongxuanzhang.mysql.service.token.SqlToken;
import org.gongxuanzhang.mysql.tool.CompareUtils;

import java.util.List;
import java.util.Objects;
import java.util.function.BiPredicate;
import java.util.function.Function;

/**
 * sql where条件之后的condition
 * todo 类型转换
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
public class Condition {

    /**
     * true 是and  false 是or
     **/
    private final boolean and;

    private final Function<JSONObject, String> left;
    private final Function<JSONObject, String> right;
    private final BiPredicate<String, String> predicate;

    private Condition(boolean and, List<SqlToken> tokenList) throws MySQLException {
        this.and = and;
        if (tokenList.size() != 3) {
            throw new MySQLException("暂时不支持复杂逻辑，只支持简单运算符");
        }
        this.left = analysisFunction(tokenList.get(0));
        this.right = analysisFunction(tokenList.get(2));
        switch (tokenList.get(1).getTokenKind()) {
            case EQUALS:
                predicate = String::equals;
                break;
            case NE:
                predicate = (s1, s2) -> !Objects.equals(s1, s2);
                break;
            case GT:
                predicate = (s1, s2) -> CompareUtils.compareString(s1, s2) > 0;
                break;
            case GTE:
                predicate = (s1, s2) -> CompareUtils.compareString(s1, s2) >= 0;
                break;
            case LT:
                predicate = (s1, s2) -> CompareUtils.compareString(s1, s2) < 0;
                break;
            case LTE:
                predicate = (s1, s2) -> CompareUtils.compareString(s1, s2) <= 0;
                break;
            default:
                throw new MySQLException("'" + tokenList.get(1).getValue() + "'暂不支持");
        }

    }

    public static Condition and(List<SqlToken> andTokens) throws MySQLException {
        return new Condition(true, andTokens);
    }

    public static Condition or(List<SqlToken> orTokens) throws MySQLException {
        return new Condition(false, orTokens);
    }

    public boolean isAnd() {
        return this.and;
    }

    public boolean isOr() {
        return !this.and;
    }

    private Function<JSONObject, String> analysisFunction(SqlToken sqlToken) throws MySQLException {
        switch (sqlToken.getTokenKind()) {
            case VAR:
                return (json) -> json.getString(sqlToken.getValue());
            case INT:
            case LITERACY:
                return (json) -> sqlToken.getValue();
            case NULL:
                return (json) -> null;
            default:
                throw new MySQLException(sqlToken.getValue() + "无法解析");
        }
    }

    public boolean getValue(JSONObject jsonObject) {
        String leftValue = left.apply(jsonObject);
        String rightValue = right.apply(jsonObject);
        return this.predicate.test(leftValue, rightValue);
    }


}
