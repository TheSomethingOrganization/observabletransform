package io.kitsuri.observabletransform;

import androidx.databinding.ObservableList;

public class ObservableLists {

    public static <From, To> TransformedObservableList<To> transform(
            ObservableList<From> from, TransformFunction<From, To> transform) {
        return new ObservableListTransform<>(from, transform);
    }

    public static <T> TransformedObservableList<T> filter(
            ObservableList<T> from, FilterFunction<T> transform) {
        return new ObservableListFilter<>(from, transform);
    }

    public static <T> BindableObservableInt size(ObservableList<T> list) {
        return new ObservableListSize<>(list);
    }

    public static <T> BindableObservableBoolean containsMatching(
            ObservableList<T> list, ObservableListContains.CheckFunction<T> check) {
        return new ObservableListContains<>(list, check);
    }

    public static <T> BindableObservableBoolean contains(ObservableList<T> list, T value) {
        return new ObservableListContains<>(list, value::equals);
    }

}
