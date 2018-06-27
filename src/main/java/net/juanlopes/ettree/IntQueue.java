package net.juanlopes.ettree;

public class IntQueue {
    private int head = 0, tail = 0;
    private int[] list;

    public IntQueue() {
        this(16);
    }

    public IntQueue(int size) {
        list = new int[size];
    }

    public boolean isEmpty() {
        return head == tail;
    }

    public void push(int v) {
        while (next(tail) == head) {
            grow();
        }
        list[tail] = v;
        tail = next(tail);
    }

    private int next(int index) {
        return (index + 1) % list.length;
    }

    private void grow() {
        int size = size();
        int newSize = list.length + (list.length << 1);

        int[] newList = new int[newSize];

        System.arraycopy(list, head, newList, 0, list.length - head);
        System.arraycopy(list, 0, newList, list.length - head, head);

        head = 0;
        tail = size;
        list = newList;
    }

    public int size() {
        return (tail - head + list.length) % list.length;
    }

    public int pop() {
        assert head != tail;
        int e = list[head];
        head = next(head);
        return e;
    }

    public void clear() {
        head = tail = 0;
    }

}
