package io.kitsuri.observabletransform;

import androidx.databinding.ObservableList;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

public class ObservableListContains<T> extends BindableObservableBoolean {

    private final ObservableList<T> source;
    private final CheckFunction<T> check;
    private final SourceListener sourceListener = new SourceListener();
    private final TreeSet<Integer> indexes = new TreeSet<>();
    private int bindCounter = 0;

    ObservableListContains(ObservableList<T> source, CheckFunction<T> check) {
        this.source = source;
        this.check = check;
    }

    private void recalculate(int pos, int size) {
        boolean oldState = get();
        for (int i = 0; i < size; i++) {
            if (check.matches(source.get(pos + i)))
                indexes.add(pos + i);
            else
                indexes.remove(pos + i);
        }
        if (get() != oldState)
            notifyChange();
    }

    @Override
    public boolean get() {
        return !indexes.isEmpty();
    }

    @Override
    public void set(boolean value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void bind() {
        if (bindCounter++ == 0) {
            source.addOnListChangedCallback(sourceListener);
            if (source instanceof Bindable)
                ((Bindable) source).bind();
            recalculate(0, source.size());
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
            recalculate(0, source.size());
        }

        @Override
        public void onItemRangeInserted(ObservableList<T> sender, int positionStart, int itemCount) {
            List<Integer> toInsertLater = new ArrayList<>();
            for (Integer i : indexes.tailSet(positionStart))
                toInsertLater.add(i + itemCount);
            indexes.tailSet(positionStart).clear();
            indexes.addAll(toInsertLater);

            recalculate(positionStart, itemCount);
        }

        @Override
        public void onItemRangeRemoved(ObservableList<T> sender, int positionStart, int itemCount) {
            if (itemCount == 0)
                return;
            boolean oldState = get();
            List<Integer> toInsertLater = new ArrayList<>();
            for (Integer i : indexes.tailSet(positionStart + itemCount))
                toInsertLater.add(i - itemCount);
            indexes.tailSet(positionStart).clear();
            indexes.addAll(toInsertLater);
            if (oldState != get())
                notifyChange();
        }

        @Override
        public void onItemRangeChanged(ObservableList<T> sender, int positionStart, int itemCount) {
            recalculate(positionStart, itemCount);
        }

        @Override
        public void onItemRangeMoved(ObservableList<T> sender, int fromPosition, int toPosition, int itemCount) {
            onItemRangeRemoved(sender, fromPosition, itemCount);
            onItemRangeInserted(sender, toPosition, itemCount);
        }

    }


    public interface CheckFunction<T> {
        boolean matches(T value);
    }

}
