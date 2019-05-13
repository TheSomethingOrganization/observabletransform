package io.mrarm.observabletransform;

import androidx.databinding.ObservableList;

public class ObservableLists {

    public static <From, To> TransformedObservableList<To> transform(
            ObservableList<From> from, TransformFunction<From, To> transform) {
        return new ObservableListTransform<>(from, transform);
    }

}
