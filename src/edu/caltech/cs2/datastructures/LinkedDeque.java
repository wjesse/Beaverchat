package edu.caltech.cs2.datastructures;

import edu.caltech.cs2.interfaces.IDeque;
import edu.caltech.cs2.interfaces.IQueue;
import edu.caltech.cs2.interfaces.IStack;

import java.util.Iterator;

public class LinkedDeque<E> implements IDeque<E>, IQueue<E>, IStack<E> {
    private Node<E> head;
    private Node<E> tail;
    private int size;

    private static class Node<E> {
        public final E data;
        public Node<E> next;
        public Node<E> back;

        public Node(E data) {
            this(data, null, null);
        }

        public Node (E data, Node<E> next, Node<E> back) {
            this.data = data;
            this.next = next;
            this.back = back;
        }
    }

    public LinkedDeque() {
        this.head = null;
        this.tail = null;
        this.size = 0;
    }

    @Override
    public void addFront(E e) {
        if (this.head == null || this.tail == null) {
            Node newNode = new Node(e);
            this.head = newNode;
            this.tail = this.head;
            this.size++;
        } else {
            Node newNode = new Node(e, this.head, null);
            this.head.back = newNode;
            this.head = newNode;
            this.size++;
        }
    }

    @Override
    public void addBack(E e) {
        if (this.head == null || this.tail == null) {
            Node newNode = new Node(e);
            this.head = newNode;
            this.tail = this.head;
            this.size++;
        }
        else {
            Node newNode = new Node(e, null, this.tail);
            this.tail.next = newNode;
            this.tail = newNode;
            this.size++;
        }
    }

    @Override
    public E removeFront() {
        if (this.size == 0) {
            return null;
        }
        E frontElem = this.head.data;
        this.head = this.head.next;
        if (this.size > 1) {
            this.head.back = null;
        } else {
            this.tail = this.head;
        }
        this.size--;
        return frontElem;
    }

    @Override
    public E removeBack() {
        if (this.size == 0) {
            return null;
        }
        E backElem = this.tail.data;
        this.tail = this.tail.back;
        if (this.size > 1) {
            this.tail.next = null;
        } else {
            this.head = this.tail;
        }
        this.size--;
        return backElem;
    }

    @Override
    public boolean enqueue(E e) {
        this.addFront(e);
        if (this.head.data != e) {
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
        if (this.tail.data != e) {
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
        return this.head.data;
    }

    @Override
    public E peekBack() {
        if (this.size == 0) {
            return null;
        }
        return this.tail.data;
    }

    @Override
    public Iterator<E> iterator() {
        return new LinkedDeque.LinkedDequeIterator();
    }

    @Override
    public int size() {
        return this.size;
    }

    public class LinkedDequeIterator implements Iterator<E> {
        private Node<E> curr;

        public LinkedDequeIterator() {
            this.curr = head;
        }

        public boolean hasNext() {
            return curr != null;
        }

        public E next() {
            E element = curr.data;
            curr = curr.next;
            return element;
        }
    }

    public String toString() {
        String output = "[";
        if (this.size == 0) {
            return "[]";
        }
        for (E elem : this) {
            output += elem + ", ";
        }
        output = output.substring(0, output.length() - 2);
        return output + "]";
    }
}
