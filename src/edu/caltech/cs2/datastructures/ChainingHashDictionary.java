package edu.caltech.cs2.datastructures;

import edu.caltech.cs2.interfaces.ICollection;
import edu.caltech.cs2.interfaces.IDeque;
import edu.caltech.cs2.interfaces.IDictionary;
import edu.caltech.cs2.interfaces.IQueue;

import java.util.Iterator;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class ChainingHashDictionary<K, V> implements IDictionary<K, V> {
    private Supplier<IDictionary<K, V>> chain;
    private IDictionary<K, V>[] backingArray;
    private static final int[] primeList = {23, 47, 97, 197, 297 ,797 , 1597, 3203,6421,12853,25717,51437,102877,205759,
            411527};
    private int primeIdx;
    private int size;

    public ChainingHashDictionary(Supplier<IDictionary<K, V>> chain) {
        this.backingArray = new IDictionary[primeList[0]];
        this.primeIdx = 0;
        this.chain = chain;
        this.size = 0;
    }

    /**
     * @param key
     * @return value corresponding to key
     */
    @Override
    public V get(K key) {
        int bucketIndex = Math.abs(key.hashCode() % this.backingArray.length);
        if (this.backingArray[bucketIndex] == null) {
            return null;
        }
        return this.backingArray[bucketIndex].get(key);
    }

    @Override
    public V remove(K key) {
        int bucketIndex = Math.abs(key.hashCode() % this.backingArray.length);
        if (this.backingArray[bucketIndex] == null) {
            return null;
        }
        V oldVal = this.backingArray[bucketIndex].remove(key);
        if (oldVal != null) {
            this.size--;
        }
        return oldVal;
    }

    @Override
    public V put(K key, V value) {
        rehash();
        int bucketIdx = Math.abs(key.hashCode() % this.backingArray.length);
        if (this.backingArray[bucketIdx] == null) {
            this.backingArray[bucketIdx] = this.chain.get();
        }
        V prevVal = this.backingArray[bucketIdx].put(key, value);
        if (prevVal == null) {
            this.size++;
        }
        return prevVal;
    }

    private void rehash() {
        if ((this.size + 1) / this.backingArray.length > 1) {
            this.primeIdx++;
            int newTableSize;
            if (this.primeIdx > primeList.length) {
                newTableSize = this.backingArray.length * 2;
            } else {
                newTableSize = primeList[this.primeIdx];
            }
            IDictionary<K, V>[] newArray = new IDictionary[newTableSize];
            for (K key : keys()) {
                V val = get(key);
                int newBucketIdx = Math.abs(key.hashCode() % newTableSize);
                if (newArray[newBucketIdx] == null) {
                    newArray[newBucketIdx] = this.chain.get();
                }
                newArray[newBucketIdx].put(key, val);
            }
            this.backingArray = newArray;
        }
    }

    @Override
    public boolean containsKey(K key) {
        int bucketIdx = Math.abs(key.hashCode() % this.backingArray.length);
        if (this.backingArray[bucketIdx] == null) {
            return false;
        }
        return this.backingArray[bucketIdx].containsKey(key);
    }

    /**
     * @param value
     * @return true if the HashDictionary contains a key-value pair with
     * this value, and false otherwise
     */
    @Override
    public boolean containsValue(V value) {
        return values().contains(value);
    }

    /**
     * @return number of key-value pairs in the HashDictionary
     */
    @Override
    public int size() {
        return this.size;
    }

    @Override
    public ICollection<K> keys() {
        ICollection<K> keys = new LinkedDeque<>();
        for (int i = 0; i < this.backingArray.length; i++) {
            if (this.backingArray[i] != null) {
                for (K key : this.backingArray[i]) {
                    keys.add(key);
                }
            }
        }
        return keys;
    }

    @Override
    public ICollection<V> values() {
        ICollection<V> vals = new LinkedDeque<>();
        for (int i = 0; i < backingArray.length; i++) {
            if (backingArray[i] != null) {
                for (V val : backingArray[i].values()) {
                    vals.add(val);
                }
            }
        }
        return vals;
    }

    /**
     * @return An iterator for all entries in the HashDictionary
     */
    @Override
    public Iterator<K> iterator() {
        return keys().iterator();
    }

}
