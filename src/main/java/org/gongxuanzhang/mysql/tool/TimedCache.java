package org.gongxuanzhang.mysql.tool;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 支持超时的缓存
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
public class TimedCache<K, V> implements Runnable {


    private final static ScheduledThreadPoolExecutor MYSQL_CACHE_CLEAR = new ScheduledThreadPoolExecutor(1, r -> {
        Thread thread = new Thread(r);
        thread.setDaemon(true);
        thread.setName("mysql_cache_clear");
        return thread;
    });

    private final Map<K, TimeOutNode<V>> cache = new ConcurrentHashMap<>();

    public TimedCache() {
        MYSQL_CACHE_CLEAR.schedule(this, 1, TimeUnit.SECONDS);
    }


    public V get(K key) {
        TimeOutNode<V> node = cache.get(key);
        if (node == null) {
            return null;
        }
        node.renew();
        return node.value;
    }


    /**
     * 设置有超时时间的session 毫秒
     *
     * @param duration 持续时间 毫秒
     **/
    public void put(K key, V value, long duration) {
        cache.put(key, new TimeOutNode<>(value, duration));
    }


    @Override
    public void run() {
        List<K> removedKeys = new ArrayList<>();
        long now = System.currentTimeMillis();
        cache.forEach((key, node) -> {
            if (node.deadline < now) {
                removedKeys.add(key);
            }
        });
        removedKeys.forEach(cache::remove);
    }

    public static class TimeOutNode<N> {

        private final N value;
        private final long duration;
        private long deadline;

        public TimeOutNode(N value, long duration) {
            this.value = value;
            this.duration = duration;
            this.deadline = System.currentTimeMillis() + duration;
        }

        public void renew() {
            this.deadline = System.currentTimeMillis() + duration;
        }
    }
}
