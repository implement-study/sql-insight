/*
 * Copyright 2023 java-mysql  and the original author or authors <gongxuanzhangmelt@gmail.com>.
 *
 * Licensed under the GNU Affero General Public License v3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://github.com/implement-study/java-mysql/blob/main/LICENSE
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.gongxuanzhang.mysql.tool;


import java.util.HashMap;
import java.util.Map;

/**
 * 用双向链表和HashMap实现的简单LRU
 *
 * @author gxz gongxuanzhang@foxmail.com
 **/
public class LRUCache<K, V> {

    private final int capacity;

    private final Map<K, Node<K, V>> cache = new HashMap<>();

    private final Node<K, V> head = new Node<>(null, null);

    private final Node<K, V> tail = new Node<>(null, null);

    public LRUCache(int capacity) {
        this.capacity = capacity;
        head.next = tail;
        tail.pre = head;
    }

    public V get(K key) {
        Node<K, V> node = cache.get(key);
        if (node == null) {
            return null;
        }
        removeNode(node);
        addToTail(node);
        return node.value;
    }

    public void put(K key, V value) {
        Node<K, V> node = new Node<>(key, value);
        if (cache.containsKey(key)) {
            Node<K, V> oldNode = cache.get(key);
            removeNode(oldNode);
            addToTail(node);
        } else {
            if (capacity == cache.size()) {
                Node<K, V> deleted = head.next;
                removeNode(deleted);
                cache.remove(deleted.key);
            }
            addToTail(node);
        }
        cache.put(key, node);

    }

    public void addToTail(Node<K, V> node) {
        tail.pre.next = node;
        node.pre = tail.pre;
        node.next = tail;
        tail.pre = node;
    }

    public void removeNode(Node<K, V> node) {
        node.pre.next = node.next;
        node.next.pre = node.pre;
    }

    static class Node<K, V> {
        K key;
        V value;
        Node<K, V> pre;
        Node<K, V> next;

        public Node(K key, V value) {
            this.key = key;
            this.value = value;
        }
    }
}
