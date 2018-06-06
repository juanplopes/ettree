package net.juanlopes.ettree;

public interface Mergeable<T> {
    void clear();

    void add(T other);
}
