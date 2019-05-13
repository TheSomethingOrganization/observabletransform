package io.mrarm.observabletransform;

import androidx.databinding.ObservableArrayList;
import androidx.databinding.ObservableList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


class ObservableListFilter<T> extends ReadOnlyListWrapper<T>
        implements TransformedObservableList<T> {

    private final ObservableList<T> source;
    private final ObservableArrayList<T> transformed;
    private final List<Integer> positions = new ArrayList<>();
    private final FilterFunction<T> filter;
    private final SourceListener sourceListener = new SourceListener();
    private int bindCounter = 0;

    ObservableListFilter(ObservableList<T> source, FilterFunction<T> filter) {
        super(new ObservableArrayList<T>());
        this.source = source;
        this.filter = filter;
        transformed = (ObservableArrayList<T>) getWrapped();
        refilter();
    }

    @Override
    public void addOnListChangedCallback(
            OnListChangedCallback<? extends ObservableList<T>> callback) {
        transformed.addOnListChangedCallback(callback);
    }

    @Override
    public void removeOnListChangedCallback(
            OnListChangedCallback<? extends ObservableList<T>> callback) {
        transformed.removeOnListChangedCallback(callback);
    }

    @Override
    public void bind() {
        if (bindCounter++ == 0) {
            source.addOnListChangedCallback(sourceListener);
            refilter();
        }
    }

    @Override
    public void unbind() {
        if (--bindCounter == 0) {
            source.removeOnListChangedCallback(sourceListener);
            transformed.clear();
            positions.clear();
        }
    }

    private static int makePositionInt(int index, boolean accepted) {
        return (index & 0x7FFFFFFF) | (accepted ? 0x80000000 : 0);
    }

    private int getInsertPosition(int index) {
        return positions.get(index) & 0x7FFFFFFF;
    }

    private boolean isPositionAccepted(int index) {
        return (positions.get(index) & 0x80000000) != 0;
    }

    private void addToInsertPositions(int fromIndex, int toIndex, int value) {
        for (int i = fromIndex; i < toIndex; i++)
            positions.set(i, makePositionInt(getInsertPosition(i) + value, isPositionAccepted(i)));
    }

    private void refilter() {
        transformed.clear();
        positions.clear();
        int j = 0;
        for (int i = 0; i < source.size(); i++) {
            boolean accepted = filter.filter(source.get(i));
            positions.add(makePositionInt(j, accepted));
            if (accepted) {
                transformed.add(source.get(i));
                j++;
            }
        }
        positions.add(makePositionInt(j, false));
    }


    private class SourceListener extends OnListChangedCallback<ObservableList<T>> {

        @Override
        public void onChanged(ObservableList<T> sender) {
            refilter();
        }

        @Override
        public void onItemRangeChanged(ObservableList<T> sender, int positionStart, int itemCount) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void onItemRangeInserted(ObservableList<T> sender, int positionStart, int itemCount) {
            int sp = getInsertPosition(positionStart);

            if (itemCount == 1) {
                boolean accepted = filter.filter(source.get(positionStart));
                positions.add(positionStart, makePositionInt(sp, accepted));
                addToInsertPositions(positionStart + 1, positions.size(), 1);
                if (accepted)
                    transformed.add(sp, source.get(positionStart));
                return;
            }

            Object[] newValues = new Object[itemCount];
            Integer[] newPositions = new Integer[itemCount];
            int j = 0;
            for (int i = 0; i < itemCount; i++) {
                boolean accepted = filter.filter(source.get(positionStart + i));
                newPositions[i] = makePositionInt(sp + j, accepted);
                if (accepted) {
                    newValues[j] = source.get(positionStart + i);
                    j++;
                }
            }
            positions.addAll(positionStart, Arrays.asList(newPositions));
            addToInsertPositions(positionStart + itemCount, positions.size(), j);
            //noinspection unchecked
            transformed.addAll(sp, (List<T>) Arrays.asList(newValues).subList(0, j));
        }

        @Override
        public void onItemRangeMoved(ObservableList<T> sender, int fromPosition, int toPosition, int itemCount) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void onItemRangeRemoved(ObservableList<T> sender, int positionStart, int itemCount) {
            if (itemCount == 1) {
                if (isPositionAccepted(positionStart))
                    transformed.remove(getInsertPosition(positionStart));
                positions.remove(positionStart);
                return;
            }

            int sp = getInsertPosition(positionStart);
            int j = 0;
            for (int i = 0; i < itemCount; i++) {
                if (isPositionAccepted(positionStart + i))
                    ++j;
            }
            transformed.subList(sp, sp + j).clear();
            positions.subList(positionStart, positionStart + itemCount).clear();
        }

    }


}
