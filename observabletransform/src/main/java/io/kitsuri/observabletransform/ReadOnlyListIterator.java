package io.kitsuri.observabletransform;

import java.util.ListIterator;

public class ReadOnlyListIterator<T> implements ListIterator<T> {

    private ListIterator<T> wrapped;

    public ReadOnlyListIterator(ListIterator<T> wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public boolean hasNext() {
        return wrapped.hasNext();
    }

    @Override
    public T next() {
        return wrapped.next();
    }

    @Override
    public boolean hasPrevious() {
        return wrapped.hasPrevious();
    }

    @Override
    public T previous() {
        return wrapped.previous();
    }

    @Override
    public int nextIndex() {
        return wrapped.nextIndex();
    }

    @Override
    public int previousIndex() {
        return wrapped.previousIndex();
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("remove");
    }

    @Override
    public void set(T t) {
        throw new UnsupportedOperationException("set");
    }

    @Override
    public void add(T t) {
        throw new UnsupportedOperationException("add");
    }
}
