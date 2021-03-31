package edu.caltech.cs2.datastructures;

import edu.caltech.cs2.interfaces.ICollection;
import edu.caltech.cs2.interfaces.IDeque;
import edu.caltech.cs2.interfaces.IDictionary;

import java.util.Iterator;

public class MoveToFrontDictionary<K, V> implements IDictionary<K,V> {
    private LinkedNode<K, V> root;
    private int size;

    private static class LinkedNode<K, V> {
        public K key;
        public V value;
        public LinkedNode<K, V> next;

        public LinkedNode(K key, V value, LinkedNode<K, V> next) {
            this.key = key;
            this.value = value;
            this.next = next;
        }
    }

    public MoveToFrontDictionary() {
        this.root = null;
        this.size = 0;
    }

    @Override
    public V remove(K key) {
        if (this.size == 0) {
            return null;
        }
        if (this.size == 1) {
            if (this.root.key.equals(key)) {
                V oldVal = this.root.value;
                this.root = null;
                this.size--;
                return oldVal;
            }
            return null;
        }
        LinkedNode<K, V> curr = this.root;
        int counter = 0;
        if (curr.key.equals(key)) {
            V oldVal = curr.value;
            this.root = curr.next;
            this.size--;
            return oldVal;
        }
        while (counter < this.size - 1) {
            counter++;
            if (curr.next.key.equals(key)) {
                V oldVal = curr.next.value;
                curr.next = curr.next.next;
                this.size--;
                return oldVal;
            }
            curr = curr.next;
        }
        return null;
    }

    @Override
    public V put(K key, V value) {
        if (this.size == 0) {
            this.root = new LinkedNode<>(key, value, null);
            this.size++;
            return null;
        }
        V oldVal = remove(key);
        this.root = new LinkedNode<>(key, value, this.root);
        this.size++;
        return oldVal;
    }

    @Override
    public boolean containsKey(K key) {
        return this.get(key) != null;
    }

    @Override
    public boolean containsValue(V value) {
        return this.values().contains(value);
    }

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public ICollection<K> keys() {
        ICollection<K> keys = new LinkedDeque<>();
        Iterator<K> iterator = iterator();
        while (iterator.hasNext()) {
            keys.add(iterator.next());
        }
        return keys;
    }

    @Override
    public ICollection<V> values() {
        LinkedNode<K, V> curr = this.root;
        ICollection<V> vals = new LinkedDeque<>();
        while (curr != null) {
            vals.add(curr.value);
            curr = curr.next;
        }
        return vals;
    }

    public V get(K key) {
        if (this.size == 0) {
            return null;
        }
        if (this.size == 1) {
            if (this.root.key.equals(key)) {
                return this.root.value;
            }
            return null;
        }
        LinkedNode<K, V> curr = this.root;
        int counter = 0;
        V oldVal = null;
        if (curr.key.equals(key)) {
            return curr.value;
        }
        while (counter < this.size - 1) {
            counter++;
            if (curr.next.key.equals(key)) {
                oldVal = curr.next.value;
                curr.next = curr.next.next;
                this.root = new LinkedNode<>(key, oldVal, this.root);
                return oldVal;
            }
            curr = curr.next;
        }
        return null;
    }


    @Override
    public Iterator<K> iterator() {
        return new LinkedIterator();
    }

    public class LinkedIterator implements Iterator<K> {
        private LinkedNode<K, V> curr;

        public LinkedIterator() {
            this.curr = root;
        }

        public boolean hasNext() {
            return curr != null;
        }

        public K next() {
            K element = curr.key;
            curr = curr.next;
            return element;
        }
    }
}
