package io.kitsuri.observabletransform;

public interface TransformFunction<From, To> {
    To transform(From from);
}