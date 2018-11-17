package net.juanlopes.ettree;

import java.io.File;
import java.io.InputStream;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.stream.StreamSupport;

public abstract class JNIWrapper {
    public static final long P = 4611686018427387847L;

    public static native long powm(long c, long a, long b);

    public static native long create(int m, int d, long seed);

    public static native void destroy(long instance);

    public static native void merge(long instance, long other);

    public static native void clear(long instance, long other);

    public static native void update(long instance, long i, long delta);

    public static native long recover(long instance, int start, int end);

    static {
        try {
            Path path = Files.createTempFile("libl0sampler", "");
            path.toFile().deleteOnExit();

            try (InputStream stream = JNIWrapper.class.getResourceAsStream("/libl0sampler.so")) {
                Files.copy(stream, path, StandardCopyOption.REPLACE_EXISTING);
            }
            System.load(path.toString());
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }
}
