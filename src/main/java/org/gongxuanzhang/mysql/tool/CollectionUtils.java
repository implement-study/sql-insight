/*
 * Copyright 2023 sql-insight  and the original author or authors <gongxuanzhangmelt@gmail.com>.
 *
 * Licensed under the GNU Affero General Public License v3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://github.com/implement-study/sql-insight/blob/main/LICENSE
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.gongxuanzhang.mysql.tool;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
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


    public static <K, V> Map<K, V> newHashMapWithExpectEntrySize(int entrySize) {
        if (entrySize < 3) {
            return new HashMap<>(4);
        }
        return new HashMap<>((int) (entrySize / 0.75F + 1));
    }


    /**
     * 把一个collection按迭代器顺序创建索引
     *
     * @param collection
     **/
    public static <V, M> Map<M, Integer> indexCollection(Collection<V> collection, Function<V, M> map) {
        if (map == null) {
            throw new NullPointerException();
        }
        Map<M, Integer> indexMap = newHashMapWithExpectEntrySize(collection.size());
        int index = 0;
        for (V v : collection) {
            indexMap.put(map.apply(v), index);
            index++;
        }
        return indexMap;
    }

    public static <V> Map<V, Integer> indexCollection(Collection<V> collection) {
        Map<V, Integer> indexMap = newHashMapWithExpectEntrySize(collection.size());
        int index = 0;
        for (V v : collection) {
            indexMap.put(v, index);
            index++;
        }
        return indexMap;
    }

}
