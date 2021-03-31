package edu.caltech.cs2.datastructures;

import edu.caltech.cs2.interfaces.ICollection;
import edu.caltech.cs2.interfaces.IDeque;
import edu.caltech.cs2.interfaces.ITrieMap;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.Iterator;

public class TrieMap<A, K extends Iterable<A>, V> implements ITrieMap<A, K, V> {
    private TrieNode<A, V> root;
    private Function<IDeque<A>, K> collector;
    private int size;

    public TrieMap(Function<IDeque<A>, K> collector) {
        this.root = null;
        this.collector = collector;
        this.size = 0;
    }
    

    @Override
    public boolean isPrefix(K key) {
        if(this.root == null) {
            return false;
        }
        Iterator<A> iterator = key.iterator();
        return isPrefix(this.root, iterator);
    }

    private boolean isPrefix(TrieNode<A, V> curr, Iterator<A> iterator) {
        if (!iterator.hasNext()) {
            return true;
        }
        if (curr.pointers.isEmpty()) {
            return false;
        }
        A elem = iterator.next();
        if (curr.pointers.containsKey(elem)) {
            return isPrefix(curr.pointers.get(elem), iterator);
        } else {
            return false;
        }
    }

    @Override
    public IDeque<V> getCompletions(K prefix) {
        LinkedDeque<V> completions = new LinkedDeque<>();
        Iterator<A> iterator = prefix.iterator();
        if (this.root == null) {
            return completions;
        }
        TrieNode<A, V> rootNode = getCompletions(this.root, iterator);
        if (rootNode == null) {
            return completions;
        }
        values(completions, rootNode);
        return completions;
    }

    private TrieNode<A, V> getCompletions(TrieNode<A, V> curr, Iterator<A> iterator) {
        if (!iterator.hasNext()) {
            return curr;
        }
        A elem = iterator.next();
        if (curr.pointers.containsKey(elem)) {
            return getCompletions(curr.pointers.get(elem), iterator);
        } else {
            return null;
        }
    }

    @Override
    public void clear() {
        this.root = null;
        this.size = 0;
    }

    @Override
    public V get(K key) {
        Iterator<A> iterator = key.iterator();
        return get(this.root, iterator);
    }

    private V get(TrieNode<A, V> curr, Iterator<A> iterator) {
        if (curr == null) {
            return null;
        }
        if (!iterator.hasNext()) {
            return curr.value;
        }
        A elem = iterator.next();
        if (curr.pointers.containsKey(elem)) {
            return get(curr.pointers.get(elem), iterator);
        } else {
            return null;
        }
    }

    @Override
    public V remove(K key) {
        if (this.root == null) {
            return null;
        }
        Iterator<A> iterator = key.iterator();
        V val = setNull(this.root, iterator);
        if (val == null) {
            return null;
        }
        Iterator<A> iterator2 = key.iterator();
        remove(this.root, iterator2);
        if (this.size == 0) {
            this.root = null;
        }
        return val;
    }

    private void remove(TrieNode<A, V> curr, Iterator<A> iterator) {
        if (!iterator.hasNext()) {
            return;
        }
        A elem = iterator.next();
        if (canDelete(curr.pointers.get(elem))) {
            curr.pointers.remove(elem);
        } else {
            remove(curr.pointers.get(elem), iterator);
        }
    }

    private V setNull(TrieNode<A, V> curr, Iterator<A> iterator) {
        if (!iterator.hasNext()) {
            V val = curr.value;
            if (val == null) {
                return null;
            } else {
                this.size--;
                curr.value = null;
                return val;
            }

        }
        A elem = iterator.next();
        if (curr.pointers.containsKey(elem)) {
            return setNull(curr.pointers.get(elem), iterator);
        } else {
            return null;
        }
    }

    private boolean canDelete(TrieNode<A, V> curr) {
        boolean delete = true;
        if (curr.pointers.isEmpty()) {
            delete = curr.value == null;
        }
        else if (curr.value != null) {
            return false;
        }
        else {
            for (A elem : curr.pointers.keySet()) {
                return canDelete(curr.pointers.get(elem));
            }
        }
        return delete;
    }

    @Override
    public V put(K key, V value) {
        if (this.root == null) {
            this.root = new TrieNode<>();
        }
        Iterator<A> iterator = key.iterator();
        return put(value, this.root, iterator);
    }

    private V put(V value, TrieNode<A, V> curr, Iterator<A> iterator) {
        if (!iterator.hasNext()) {
            V prevVal = curr.value;
            if (prevVal == null) {
                size++;
            }
            curr.value = value;
            return prevVal;
        }
        A elem = iterator.next();
        if (!curr.pointers.containsKey(elem)) {
            curr.pointers.put(elem, new TrieNode<A, V>());
        }
        return put(value, curr.pointers.get(elem), iterator);
    }

    @Override
    public boolean containsKey(K key) {
        if (this.root == null) {
            return false;
        }
        Iterator<A> iterator = key.iterator();
        return containsKey(this.root, iterator);
    }

    private boolean containsKey(TrieNode<A, V> curr, Iterator<A> iterator) {
        if (!iterator.hasNext()) {
            return curr.value != null;
        }
        else if (curr.pointers.isEmpty()) {
            return false;
        } else {
            A elem = iterator.next();
            if (curr.pointers.containsKey(elem)) {
                return containsKey(curr.pointers.get(elem), iterator);
            } else {
                return false;
            }
        }
    }

    @Override
    public boolean containsValue(V value) {
        for (V val : values()) {
            if (val.equals(value)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public ICollection<K> keys() {
        LinkedDeque<K> result = new LinkedDeque<>();
        if (this.root == null) {
            return result;
        }
        keys(result, this.root, this.collector, new LinkedDeque<A>());
        return result;
    }

    private void keys(ICollection<K> result, TrieNode<A, V> curr, Function<IDeque<A>, K> collector, IDeque<A> acc) {
        if (curr.value != null) {
            result.add(collector.apply(acc));
        }
        for (A elem : curr.pointers.keySet()) {
            acc.addBack(elem);
            keys(result, curr.pointers.get(elem), collector, acc);
            acc.removeBack();
        }
    }

    @Override
    public ICollection<V> values() {
        LinkedDeque<V> result = new LinkedDeque<>();
        if (this.root == null) {
            return result;
        }
        values(result, this.root);
        return result;
    }

    private void values(ICollection<V> result, TrieNode<A, V> curr) {
        if (curr.value != null) {
            result.add(curr.value);
        }
        for (A elem : curr.pointers.keySet()) {
            values(result, curr.pointers.get(elem));
        }
    }

    @Override
    public Iterator<K> iterator() {
        return new TrieMapIterator();
    }

    public class TrieMapIterator implements Iterator<K> {
        private Iterator<K> iterator;

        public TrieMapIterator() {
            this.iterator = keys().iterator();
        }

        public boolean hasNext() {
            return this.iterator.hasNext();
        }

        public K next() {
            return this.iterator.next();
        }
    }
    
    private static class TrieNode<A, V> {
        public final Map<A, TrieNode<A, V>> pointers;
        public V value;

        public TrieNode() {
            this(null);
        }

        public TrieNode(V value) {
            this.pointers = new HashMap<>();
            this.value = value;
        }

        @Override
        public String toString() {
            StringBuilder b = new StringBuilder();
            if (this.value != null) {
                b.append("[" + this.value + "]-> {\n");
                this.toString(b, 1);
                b.append("}");
            }
            else {
                this.toString(b, 0);
            }
            return b.toString();
        }

        private String spaces(int i) {
            StringBuilder sp = new StringBuilder();
            for (int x = 0; x < i; x++) {
                sp.append(" ");
            }
            return sp.toString();
        }

        protected boolean toString(StringBuilder s, int indent) {
            boolean isSmall = this.pointers.entrySet().size() == 0;

            for (Map.Entry<A, TrieNode<A, V>> entry : this.pointers.entrySet()) {
                A idx = entry.getKey();
                TrieNode<A, V> node = entry.getValue();

                if (node == null) {
                    continue;
                }

                V value = node.value;
                s.append(spaces(indent) + idx + (value != null ? "[" + value + "]" : ""));
                s.append("-> {\n");
                boolean bc = node.toString(s, indent + 2);
                if (!bc) {
                    s.append(spaces(indent) + "},\n");
                }
                else if (s.charAt(s.length() - 5) == '-') {
                    s.delete(s.length() - 5, s.length());
                    s.append(",\n");
                }
            }
            if (!isSmall) {
                s.deleteCharAt(s.length() - 2);
            }
            return isSmall;
        }
    }
}
