package org.gongxuanzhang.mysql.tool;

/**
 * 只有一个键值对
 *
 * @author gongxuanzhang
 */
public class Pair<K, V> {

    private K key;

    private V value;

    private Pair() {

    }

    private Pair(K key, V value) {
        this.key = key;
        this.value = value;
    }

    public static <K, V> Pair<K, V> of(K key, V value) {
        return new Pair<>(key,value);
    }

    public K getKey() {
        return this.key;
    }

    public V getValue() {
        return this.value;
    }
}
