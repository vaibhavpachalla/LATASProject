package com.precisionhawk.latas.client.util;

public class ExceptionUtil {

    public interface Runnable {
        void run() throws Exception;
    }

    public interface Supplier<T> {
        T get() throws Exception;
    }

    public static void duckRunnable(Runnable runnable) {
        try {
            runnable.run();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T duckSupplier(Supplier<T> supplier) {
        try {
            return supplier.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
