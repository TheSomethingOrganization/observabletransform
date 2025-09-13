package io.kitsuri.observabletransform;

public interface FilterFunction<T> {
    boolean filter(T value);
}
