package io.mrarm.observabletransform;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

class ReadOnlyListWrapper<T> extends ReadOnlyList<T> {

    private List<T> wrapped;

    ReadOnlyListWrapper(List<T> wrapped) {
        this.wrapped = wrapped;
    }

    public List<T> getWrapped() {
        return wrapped;
    }

    @Override
    public int size() {
        return wrapped.size();
    }

    @Override
    public boolean isEmpty() {
        return wrapped.isEmpty();
    }

    @Override
    public boolean contains(@Nullable Object o) {
        return wrapped.contains(o);
    }

    @NonNull
    @Override
    public Iterator<T> iterator() {
        return new ReadOnlyIterator<>(wrapped.iterator());
    }

    @Nullable
    @Override
    public Object[] toArray() {
        return wrapped.toArray();
    }

    @Override
    public <T1> T1[] toArray(@Nullable T1[] a) {
        return wrapped.toArray(a);
    }

    @Override
    public boolean containsAll(@NonNull Collection<?> c) {
        return wrapped.containsAll(c);
    }

    @Override
    public T get(int index) {
        return wrapped.get(index);
    }

    @Override
    public int indexOf(@Nullable Object o) {
        return wrapped.indexOf(o);
    }

    @Override
    public int lastIndexOf(@Nullable Object o) {
        return wrapped.lastIndexOf(o);
    }

    @NonNull
    @Override
    public ListIterator<T> listIterator() {
        return new ReadOnlyListIterator<>(wrapped.listIterator());
    }

    @NonNull
    @Override
    public ListIterator<T> listIterator(int index) {
        return new ReadOnlyListIterator<>(wrapped.listIterator(index));
    }

    @NonNull
    @Override
    public List<T> subList(int fromIndex, int toIndex) {
        return new ReadOnlyListWrapper<>(wrapped.subList(fromIndex, toIndex));
    }

}
