package io.mrarm.observabletransform;

import androidx.databinding.ObservableList;

class ObservableListSize<T> extends BindableObservableInt {

    private final ObservableList<T> source;
    private final SourceListener sourceListener = new SourceListener();
    private int bindCounter = 0;

    ObservableListSize(ObservableList<T> source) {
        this.source = source;
    }

    @Override
    public int get() {
        return source.size();
    }

    @Override
    public void set(int value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void bind() {
        if (bindCounter++ == 0) {
            source.addOnListChangedCallback(sourceListener);
            if (source instanceof Bindable)
                ((Bindable) source).bind();
            notifyChange();
        }
    }

    @Override
    public void unbind() {
        if (--bindCounter == 0) {
            if (source instanceof Bindable)
                ((Bindable) source).unbind();
            source.removeOnListChangedCallback(sourceListener);
        }
    }


    private class SourceListener extends ObservableList.OnListChangedCallback<ObservableList<T>> {

        @Override
        public void onChanged(ObservableList<T> sender) {
            notifyChange();
        }

        @Override
        public void onItemRangeInserted(ObservableList<T> sender, int positionStart, int itemCount) {
            notifyChange();
        }

        @Override
        public void onItemRangeRemoved(ObservableList<T> sender, int positionStart, int itemCount) {
            notifyChange();
        }

        @Override
        public void onItemRangeChanged(ObservableList<T> sender, int positionStart, int itemCount) {
        }

        @Override
        public void onItemRangeMoved(ObservableList<T> sender, int fromPosition, int toPosition, int itemCount) {
        }

    }

}
