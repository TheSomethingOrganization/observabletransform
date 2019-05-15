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
            if (source instanceof Bindable)
                ((Bindable) source).bind();
            reapply();
        }
    }

    @Override
    public void unbind() {
        if (--bindCounter == 0) {
            if (source instanceof Bindable)
                ((Bindable) source).unbind();
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
        for (int i = fromIndex; i < toIndex; i++) {
            positions.set(i, makePositionInt(getInsertPosition(i) + value, isPositionAccepted(i)));
        }
    }

    @Override
    public void reapply() {
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

    @Override
    public void reapply(int positionStart, int itemCount) {
        sourceListener.onItemRangeChanged(source, positionStart, itemCount);
    }


    private class SourceListener extends OnListChangedCallback<ObservableList<T>> {

        @Override
        public void onChanged(ObservableList<T> sender) {
            reapply();
        }

        @Override
        public void onItemRangeChanged(ObservableList<T> sender, int positionStart, int itemCount) {
            int sp = getInsertPosition(positionStart);

            if (itemCount == 1) {
                boolean accepted = filter.filter(source.get(positionStart));
                boolean wasAccepted = isPositionAccepted(positionStart);
                if (accepted && wasAccepted) {
                    transformed.set(sp, source.get(positionStart));
                } else if (accepted /* && !wasAccepted - always true */) {
                    transformed.add(sp, source.get(positionStart));
                    positions.set(positionStart, makePositionInt(sp, true));
                    addToInsertPositions(positionStart + 1, positions.size(), 1);
                } else if (/* !accepted - always true && */wasAccepted) {
                    transformed.remove(sp);
                    positions.set(positionStart, makePositionInt(sp, false));
                    addToInsertPositions(positionStart + 1, positions.size(),-1);
                }
                return;
            }

            boolean[] newAccepted = new boolean[itemCount];
            for (int i = 0; i < itemCount; i++)
                newAccepted[i] = filter.filter(source.get(positionStart + i));

            int oldAcceptedCount = 0;
            for (int i = 0; i < itemCount; i++) {
                if (isPositionAccepted(positionStart + i))
                    ++oldAcceptedCount;
            }

            // Handle deletion
            int deleteStart = -1, deleteEnd = -1;
            for (int i = itemCount - 1; i >= 0; --i) {
                if (!isPositionAccepted(positionStart + i))
                    continue;
                if (!newAccepted[i]) {
                    int j = getInsertPosition(positionStart + i);
                    // mark for deletion
                    if (deleteStart != -1)
                        deleteStart = j;
                    else
                        deleteStart = deleteEnd = j;
                } else if (deleteStart != -1) {
                    if (deleteStart == deleteEnd)
                        transformed.remove(deleteStart);
                    else
                        transformed.subList(deleteStart, deleteEnd + 1).clear();
                    deleteStart = deleteEnd = -1;
                }
            }
            if (deleteStart != -1) {
                if (deleteStart == deleteEnd)
                    transformed.remove(deleteStart);
                else
                    transformed.subList(deleteStart, deleteEnd + 1).clear();
            }
            // Now there are only items in the following states: (wasAccepted, isAccepted), (!wasAccepted, isAccepted), (!wasAccepted, !isAccepted)

            // Handle insertion and index update
            List<T> insertBuf = new ArrayList<>();
            int insertBufStart = -1;
            int j = sp;
            for (int i = 0; i < itemCount; i++) {
                boolean wasAccepted = isPositionAccepted(positionStart + i);
                positions.set(positionStart + i, makePositionInt(j, newAccepted[i]));
                if (!newAccepted[i])
                    continue;
                if (!wasAccepted) {
                    if (insertBufStart == -1)
                        insertBufStart = j;
                    insertBuf.add(source.get(positionStart + i));
                } else if (insertBufStart != -1) {
                    transformed.addAll(insertBufStart, insertBuf);
                    insertBuf.clear();
                    insertBufStart = -1;
                }
                ++j;
            }
            if (insertBufStart != -1) {
                transformed.addAll(insertBufStart, insertBuf);
                insertBuf.clear();
            }

            addToInsertPositions(positionStart + itemCount, positions.size(), j - (sp + oldAcceptedCount));
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
