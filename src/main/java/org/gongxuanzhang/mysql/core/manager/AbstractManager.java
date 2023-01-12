package org.gongxuanzhang.mysql.core.manager;

import org.gongxuanzhang.mysql.exception.MySQLException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 管理器的骨架实现
 * 用一个 concurrentHashMap管理
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
public abstract class AbstractManager<T> implements MySQLManager<T> {


    private final Map<String, T> cache = new ConcurrentHashMap<>();

    public AbstractManager() throws MySQLException {
    }

    @Override
    public void register(T t) {
        cache.put(toId(t), t);
    }

    @Override
    public T select(String name) {
        return cache.get(name);
    }

    @Override
    public List<T> getAll() {
        return new ArrayList<>(cache.values());
    }


    @Override
    public void refresh() throws MySQLException {
        cache.clear();
        init();
    }

    /**
     * 初始化内容，在构造和refresh的时候会触发
     *
     * @throws MySQLException 初始化发生异常触发
     **/
    protected abstract void init() throws MySQLException;
}
