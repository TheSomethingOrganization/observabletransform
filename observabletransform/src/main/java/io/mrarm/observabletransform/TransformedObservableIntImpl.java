package io.mrarm.observabletransform;

import androidx.databinding.Observable;

class TransformedObservableIntImpl<T extends Observable> extends TransformedObservableInt {

    private final T source;
    private final Callback<T> callback;
    private final ObservablePropertyChangedCallback propertyCallback =
            new ObservablePropertyChangedCallback(this);
    private int bindCounter = 0;

    public TransformedObservableIntImpl(T source, Callback<T> callback) {
        this.source = source;
        this.callback = callback;
    }

    @Override
    public void reapply() {
        super.set(callback.transform(source));
    }

    @Override
    public void bind() {
        if (bindCounter++ == 0) {
            if (source instanceof Bindable)
                ((Bindable) source).bind();
            source.addOnPropertyChangedCallback(propertyCallback);
            reapply();
        }
    }

    @Override
    public void unbind() {
        if (--bindCounter == 0) {
            source.removeOnPropertyChangedCallback(propertyCallback);
            if (source instanceof Bindable)
                ((Bindable) source).unbind();
        }
    }

    @Override
    public void set(int value) {
        throw new UnsupportedOperationException();
    }


    public interface Callback<T extends Observable> {
        int transform(T source);
    }

}
