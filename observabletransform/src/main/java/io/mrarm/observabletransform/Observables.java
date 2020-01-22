package io.mrarm.observabletransform;

import androidx.databinding.Observable;
import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;
import androidx.databinding.ObservableInt;

public class Observables {

    public static TransformedObservableBoolean booleanTransform(ObservableInt source, IntBoolTransform transform) {
        return new TransformedObservableBooleanImpl<>(source, (x) -> transform.transform(source.get()));
    }
    public static TransformedObservableBoolean booleanTransform(ObservableBoolean source, BoolBoolTransform transform) {
        return new TransformedObservableBooleanImpl<>(source, (x) -> transform.transform(source.get()));
    }
    public static <T> TransformedObservableBoolean booleanTransform(ObservableField<T> source, ObjectBoolTransform<T> transform) {
        return new TransformedObservableBooleanImpl<>(source, (x) -> transform.transform(source.get()));
    }
    public static TransformedObservableBoolean booleanTransform(BoolTransform transform, Observable... dependencies) {
        return new TransformedObservableBooleanMultiImpl(dependencies, transform::transform);
    }

    public static TransformedObservableInt intTransform(ObservableInt source, IntIntTransform transform) {
        return new TransformedObservableIntImpl<>(source, (x) -> transform.transform(source.get()));
    }
    public static TransformedObservableInt intTransform(ObservableBoolean source, BoolIntTransform transform) {
        return new TransformedObservableIntImpl<>(source, (x) -> transform.transform(source.get()));
    }
    public static <T> TransformedObservableInt intTransform(ObservableField<T> source, ObjectIntTransform<T> transform) {
        return new TransformedObservableIntImpl<>(source, (x) -> transform.transform(source.get()));
    }


    public interface BoolBoolTransform {
        boolean transform(boolean value);
    }
    public interface IntBoolTransform {
        boolean transform(int value);
    }
    public interface ObjectBoolTransform<T> {
        boolean transform(T value);
    }
    public interface BoolTransform {
        boolean transform();
    }
    public interface BoolIntTransform {
        int transform(boolean value);
    }
    public interface IntIntTransform {
        int transform(int value);
    }
    public interface ObjectIntTransform<T> {
        int transform(T value);
    }

}
