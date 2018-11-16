package net.juanlopes.ettree;

import java.io.Closeable;

public interface L0Sampler<T extends L0Sampler<T>> extends Mergeable<T>, Closeable {
    void update(long i, long delta);

    long recover();

    long recover(int start, int end);

    int bytes();

    @Override
    void clear();

    void clearTo(T sampler);

    @Override
    void add(T that);

    void close();
}
