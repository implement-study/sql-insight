package org.gongxuanzhang.mysql.tool;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @author gxz gongxuanzhang@foxmail.com
 **/
public abstract class CollectionUtils {

    /**
     * 如果不为空 遍历集合
     *
     * @param collection 集合 可以为空
     * @param consumer   遍历的操作
     **/
    public static <T> boolean foreachIfNotEmpty(Collection<T> collection, Consumer<T> consumer) {
        if (collection != null && !collection.isEmpty()) {
            collection.forEach(consumer);
            return true;
        }
        return false;
    }

    /**
     * 如果不为空 遍历集合
     *
     * @param collection 集合 可以为空
     * @param consumer   遍历的操作
     **/
    public static <T> boolean foreachIfNotEmpty(Collection<T> collection, BiConsumer<T, Integer> consumer) {
        if (collection != null && !collection.isEmpty()) {
            Iterator<T> iterator = collection.iterator();
            int i = 0;
            while (iterator.hasNext()) {
                T next = iterator.next();
                consumer.accept(next, i);
                i++;
            }
            return true;
        }
        return false;
    }

    public static <T> boolean foreachIfNotEmpty(T[] array, Consumer<T> consumer) {
        if (array != null && array.length > 0) {
            for (T t : array) {
                consumer.accept(t);
            }
            return true;
        }
        return false;
    }

    public static <T> boolean foreachIfNotEmpty(T[] array, BiConsumer<T, Integer> consumer) {
        if (array != null && array.length > 0) {
            for (int i = 0; i < array.length; i++) {
                consumer.accept(array[i], i);
            }
            return true;
        }
        return false;
    }

    public static <T> List<T> arrayToList(T[] array) {
        if (array == null || array.length == 0) {
            return Collections.emptyList();
        }
        return new ArrayList<>(Arrays.asList(array));
    }

    public static <T, R> List<R> arrayToList(T[] array, Function<T, R> map) {
        if (array == null || array.length == 0) {
            return Collections.emptyList();
        }
        List<R> result = new ArrayList<>();
        for (T t : array) {
            result.add(map.apply(t));
        }
        return result;
    }
}
