package io.mrarm.observabletransform;

public interface TransformFunction<From, To> {
    To transform(From from);
}