package edu.caltech.cs2.datastructures;

import edu.caltech.cs2.interfaces.IDictionary;
import edu.caltech.cs2.interfaces.IPriorityQueue;

import java.util.Iterator;

public class MinFourHeap<E> implements IPriorityQueue<E> {

    private static final int DEFAULT_CAPACITY = 10;

    private int size;
    private PQElement<E>[] data;
    private IDictionary<E, Integer> keyToIndexMap;

    /**
     * Creates a new empty heap with DEFAULT_CAPACITY.
     */
    public MinFourHeap() {
        this.size = 0;
        this.data = new PQElement[DEFAULT_CAPACITY];
        this.keyToIndexMap = new ChainingHashDictionary<>(MoveToFrontDictionary::new);
    }

    @Override
    public void increaseKey(PQElement<E> key) {
        if (this.keyToIndexMap.get(key.data) == null) {
            throw new IllegalArgumentException();
        } else {
            int idx = this.keyToIndexMap.get(key.data);
            this.data[idx] = key;
            this.keyToIndexMap.put(key.data, idx);
            percolateDown(this.data[idx]);
        }
    }

    @Override
    public void decreaseKey(PQElement<E> key) {
        if (this.keyToIndexMap.get(key.data) == null) {
            throw new IllegalArgumentException();
        } else {
            int idx = this.keyToIndexMap.get(key.data);
            this.data[idx] = key;
            this.keyToIndexMap.put(key.data, idx);
            percolateUp(this.data[idx]);
        }
    }

    private int getParentIdx(int idx) {
        if (idx == 0) {
            return 0;
        }
        if (idx % 4 == 0) {
            return idx/4 - 1;
        } else {
            return idx/4;
        }
    }

    private int[] getChildIdx(int idx) {
        int[] children = new int[] {Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE};
        int counter = 0;
        for (int a = idx * 4 + 1; a <= idx * 4 + 4; a++) {
            if (a < this.size) {
                children[counter] = a;
                counter++;
            } else {
                break;
            }
        }
        return children;
    }

    private void resize(){
        PQElement<E>[] newBackingArray = new PQElement[this.data.length * 2];
        for (int i = 0; i < this.data.length; i++) {
            newBackingArray[i] = this.data[i];
        }
        this.data = newBackingArray;
    }

    @Override
    public boolean enqueue(PQElement<E> epqElement) {
        //resize
        if (this.size >= this.data.length) {
            this.resize();
        }
        if (this.keyToIndexMap.containsKey(epqElement.data)) {
            throw new IllegalArgumentException();
        }
        this.data[this.size] = epqElement;
        this.keyToIndexMap.put(epqElement.data, this.size);
        if (this.size != 0) {
            percolateUp(this.data[this.size]);
        }
        this.size++;
        return true;
    }

    private void percolateUp(PQElement<E> node) {
        int currIdx = this.keyToIndexMap.get(node.data);
        int parentIdx = getParentIdx(currIdx);
        while (node.priority < this.data[parentIdx].priority) {
            PQElement<E> prevNode = this.data[parentIdx];
            this.data[parentIdx] = node;
            this.data[currIdx] = prevNode;
            this.keyToIndexMap.put(node.data, parentIdx);
            this.keyToIndexMap.put(prevNode.data, currIdx);
            currIdx = parentIdx;
            parentIdx = getParentIdx(currIdx);
        }
    }

    private void percolateDown(PQElement<E> node) {
        int currIdx = this.keyToIndexMap.get(node.data);
        int[] childrenIdx = getChildIdx(currIdx);
        while ((childrenIdx[0] < this.size && node.priority > this.data[childrenIdx[0]].priority) ||
                (childrenIdx[1] < this.size && node.priority > this.data[childrenIdx[1]].priority) ||
                (childrenIdx[2] < this.size && node.priority > this.data[childrenIdx[2]].priority) ||
                (childrenIdx[3] < this.size && node.priority > this.data[childrenIdx[3]].priority)) {
            int childIdx = childrenIdx[0];
            for (int i = 1; i < childrenIdx.length; i++) {
                if (childrenIdx[i] < this.size && this.data[childrenIdx[i]].priority < this.data[childIdx].priority) {
                    childIdx = childrenIdx[i];
                }
            }
            PQElement<E> prevNode = this.data[childIdx];
            this.data[childIdx] = node;
            this.data[currIdx] = prevNode;
            this.keyToIndexMap.put(node.data, childIdx);
            this.keyToIndexMap.put(prevNode.data, currIdx);
            currIdx = childIdx;
            childrenIdx = getChildIdx(currIdx);
        }
    }

    @Override
    public PQElement<E> dequeue() {
        if (this.size == 0) {
            return null;
        }
        PQElement<E> tempNode = this.data[0];
        this.data[0] = this.data[this.size - 1];
        this.keyToIndexMap.remove(tempNode.data);
        this.size--;
        if (this.size != 0) {
            this.keyToIndexMap.put(this.data[0].data, 0);
            percolateDown(this.data[0]);
        }
        return tempNode;
    }

    @Override
    public PQElement<E> peek() {
        return this.data[0];
    }

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public Iterator<PQElement<E>> iterator() {
        return null;
    }
}