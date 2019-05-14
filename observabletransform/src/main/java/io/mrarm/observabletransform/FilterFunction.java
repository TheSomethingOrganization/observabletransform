package io.mrarm.observabletransform;

public interface FilterFunction<T> {
    boolean filter(T value);
}
