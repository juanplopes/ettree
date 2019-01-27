package net.juanlopes.ettree;

public class Slot implements Mergeable<Slot> {
    private int value;
    private int sum;
    private String desc;

    public Slot(int value) {
        this(value, null);
    }

    public Slot(int value, String desc) {
        this.value = value;
        this.sum = value;
        this.desc = desc;
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
        return sum + "(" + value + ")" + (desc != null ? " " + desc : "");
    }

    public int value() {
        return value;
    }

    public int sum() {
        return sum;
    }

    public String desc() {
        return desc;
    }

    public void set(int value) {
        this.value = value;
    }
}
