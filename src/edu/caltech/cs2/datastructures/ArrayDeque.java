package edu.caltech.cs2.datastructures;

import edu.caltech.cs2.interfaces.IDeque;
import edu.caltech.cs2.interfaces.IQueue;
import edu.caltech.cs2.interfaces.IStack;

import java.util.Iterator;

public class ArrayDeque<E> implements IDeque<E>, IQueue<E>, IStack<E> {
    private static final int DEFAULT_CAPACITY = 10;
    private static final int GROW_FACTOR = 2;
    private int size;
    private E[] data;

    public ArrayDeque(int initialCapacity) {
        this.data = (E[])new Object[initialCapacity];
        this.size = 0;
    }

    public ArrayDeque() {
        this(DEFAULT_CAPACITY);
    }

    private void ensureCapacity(int size) {
        if (this.data.length <= size) {
            E[] newData = (E[])new Object[(int)(this.data.length * GROW_FACTOR)];
            for (int i = 0; i < this.size; i++) {
                newData[i] = this.data[i];
            }
            this.data = newData;
        }
    }

    @Override
    public void addFront(E e) {
        this.ensureCapacity(this.size);
        for (int i = this.size; i > 0; i--) {
            this.data[i] = this.data[i-1];
        }
        this.data[0] = e;
        this.size++;
    }

    @Override
    public void addBack(E e) {
        this.ensureCapacity(this.size);
        this.data[this.size] = e;
        this.size++;
    }

    @Override
    public E removeFront() {
        if (this.size == 0) {
            return null;
        }
        E frontElem = this.data[0];
        for (int i = 0; i < this.size - 1; i++) {
            this.data[i] = this.data[i+1];
        }
        this.size--;
        return frontElem;
    }

    @Override
    public E removeBack() {
        if (this.size == 0) {
            return null;
        }
        E backElem = this.data[this.size - 1];
        this.size--;
        return backElem;
    }

    @Override
    public boolean enqueue(E e) {
        this.addFront(e);
        if (this.data[0] != e) {
            return false;
        }
        return true;
    }

    @Override
    public E dequeue() {
        return this.removeBack();
    }

    @Override
    public boolean push(E e) {
        this.addBack(e);
        if (this.data[this.size - 1] != e) {
            return false;
        }
        return true;
    }

    @Override
    public E pop() {
        return this.removeBack();
    }

    @Override
    public E peek() {
        return this.peekBack();
    }

    @Override
    public E peekFront() {
        if (this.size == 0) {
            return null;
        }
        return this.data[0];
    }

    @Override
    public E peekBack() {
        if (this.size == 0) {
            return null;
        }
        return this.data[this.size - 1];
    }

    @Override
    public Iterator<E> iterator() {
        return new ArrayDequeIterator();
    }

    private class ArrayDequeIterator implements Iterator<E> {
        private int currentIndex;

        public ArrayDequeIterator() {
            this.currentIndex = 0;
        }

        public boolean hasNext() {
            return this.currentIndex < ArrayDeque.this.size;
        }

        public E next() {
            E element = ArrayDeque.this.data[this.currentIndex];
            this.currentIndex++;
            return element;
        }

    }

    @Override
    public int size() {
        return this.size;
    }

    public String toString() {
        String output = "[";

        if (this.size == 0) {
            return "[]";
        }
        for (int i = 0; i < this.size; i++) {
            output += this.data[i] + ", ";
        }
        output = output.substring(0, output.length() - 2);
        return output + "]";
    }
}

