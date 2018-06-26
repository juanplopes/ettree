package net.juanlopes.ettree;

public class Slot implements Mergeable<Slot> {
    private int value;
    private int sum;

    public Slot(int value) {
        this.value = value;
        this.sum = value;
    }

    @Override
    public void clear() {
        sum = value;
    }

    @Override
    public void add(Slot other) {
        sum += other.sum;
    }

    @Override
    public String toString() {
        return sum + "(" + value + ")";
    }

    public int value() {
        return value;
    }

    public int sum() {
        return sum;
    }

    public void set(int value) {
        this.value = value;
    }
}
