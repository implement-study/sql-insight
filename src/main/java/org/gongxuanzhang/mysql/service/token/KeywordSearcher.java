package org.gongxuanzhang.mysql.service.token;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 关键词 搜索
 * 根据传入字符串搜索匹配的关键字
 * 如果匹配不到返回字符类型
 *
 * @author gongxuanzhang
 */
public class KeywordSearcher {

    private static final Map<String, TokenKind> CACHE;

    static {
        Map<String, TokenKind> map = new HashMap<>();
        for (TokenKind value : TokenKind.values()) {
            map.put(value.toString().toUpperCase(), value);
        }
        CACHE = Collections.unmodifiableMap(map);
    }


    public static TokenKind search(String str) {
        return CACHE.getOrDefault(str.toUpperCase(), TokenKind.VAR);
    }


}
