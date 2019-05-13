package io.mrarm.observabletransform;

import androidx.databinding.ListChangeRegistry;
import androidx.databinding.ObservableList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;


class ObservableListTransform<From, To> extends ReadOnlyListWrapper<To>
        implements TransformedObservableList<To> {

    private final ObservableList<From> source;
    private final ArrayList<To> transformed;
    private final TransformFunction<From, To> transform;
    private ListChangeRegistry listeners;
    private final SourceListener sourceListener = new SourceListener();
    private int bindCounter = 0;

    ObservableListTransform(ObservableList<From> source, TransformFunction<From, To> transform) {
        super(new ArrayList<To>());
        this.source = source;
        this.transform = transform;
        transformed = (ArrayList<To>) getWrapped();
    }

    @Override
    public void addOnListChangedCallback(
            OnListChangedCallback<? extends ObservableList<To>> callback) {
        if (listeners == null)
            listeners = new ListChangeRegistry();
        listeners.add(callback);
    }

    @Override
    public void removeOnListChangedCallback(
            OnListChangedCallback<? extends ObservableList<To>> callback) {
        if (listeners != null)
            listeners.remove(callback);
    }

    @Override
    public void bind() {
        if (bindCounter++ == 0) {
            source.addOnListChangedCallback(sourceListener);

            if (transformed.size() != 0)
                throw new IllegalStateException("transformed.size() != 0");
            transformed.ensureCapacity(source.size());
            for (int i = 0; i < source.size(); i++)
                transformed.add(transform.transform(source.get(i)));
        }
    }

    @Override
    public void unbind() {
        if (--bindCounter == 0) {
            source.removeOnListChangedCallback(sourceListener);
            transformed.clear();
        }
    }


    private class SourceListener extends OnListChangedCallback<ObservableList<From>> {

        @Override
        public void onChanged(ObservableList<From> sender) {
            if (listeners != null)
                listeners.notifyChanged(ObservableListTransform.this);
        }

        @Override
        public void onItemRangeChanged(ObservableList<From> sender, int positionStart, int itemCount) {
            for (int i = 0; i < itemCount; i++)
                transformed.set(positionStart + i,
                        transform.transform(source.get(positionStart + i)));
            if (listeners != null)
                listeners.notifyChanged(ObservableListTransform.this, positionStart, itemCount);
        }

        @Override
        public void onItemRangeInserted(ObservableList<From> sender, int positionStart, int itemCount) {
            if (itemCount == 1) {
                transformed.add(positionStart, transform.transform(source.get(positionStart)));
            } else {
                Object[] values = new Object[itemCount];
                for (int i = 0; i < itemCount; i++)
                    values[i] = transform.transform(source.get(positionStart + i));
                //noinspection unchecked
                transformed.addAll(positionStart, (Collection<? extends To>) Arrays.asList(values));
            }
            if (listeners != null)
                listeners.notifyInserted(ObservableListTransform.this, positionStart, itemCount);
        }

        @Override
        public void onItemRangeMoved(ObservableList<From> sender, int fromPosition, int toPosition, int itemCount) {
            // This is unused by ObservableArrayList but let's still provide a fallback impl
            List<To> tmpSource = transformed.subList(fromPosition, fromPosition + itemCount);
            List<To> tmp = new ArrayList<>(tmpSource);
            tmpSource.clear();
            transformed.addAll(toPosition, tmp);
            if (listeners != null)
                listeners.notifyMoved(ObservableListTransform.this, fromPosition, toPosition, itemCount);
        }

        @Override
        public void onItemRangeRemoved(ObservableList<From> sender, int positionStart, int itemCount) {
            transformed.subList(positionStart, positionStart + itemCount).clear();
            if (listeners != null)
                listeners.notifyRemoved(ObservableListTransform.this, positionStart, itemCount);
        }

    }

}
