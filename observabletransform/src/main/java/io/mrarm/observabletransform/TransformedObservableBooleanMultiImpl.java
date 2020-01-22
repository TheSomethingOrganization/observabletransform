package io.mrarm.observabletransform;

import androidx.databinding.Observable;

class TransformedObservableBooleanMultiImpl extends TransformedObservableBoolean {

    private final Observable[] sources;
    private final Callback callback;
    private final ObservablePropertyChangedCallback propertyCallback =
            new ObservablePropertyChangedCallback(this);
    private int bindCounter = 0;

    TransformedObservableBooleanMultiImpl(Observable[] sources, Callback callback) {
        this.sources = sources;
        this.callback = callback;
    }

    @Override
    public void reapply() {
        super.set(callback.transform());
    }

    @Override
    public void bind() {
        if (bindCounter++ == 0) {
            for (Observable source : sources) {
                if (source instanceof Bindable)
                    ((Bindable) source).bind();
                source.addOnPropertyChangedCallback(propertyCallback);
            }
            reapply();
        }
    }

    @Override
    public void unbind() {
        if (--bindCounter == 0) {
            for (Observable source : sources) {
                source.removeOnPropertyChangedCallback(propertyCallback);
                if (source instanceof Bindable)
                    ((Bindable) source).unbind();
            }
        }
    }

    @Override
    public void set(boolean value) {
        throw new UnsupportedOperationException();
    }


    public interface Callback {
        boolean transform();
    }

}
