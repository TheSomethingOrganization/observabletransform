package io.mrarm.observabletransform;

import java.util.Iterator;

class ReadOnlyIterator<T> implements Iterator<T> {

    private Iterator<T> wrapped;

    public ReadOnlyIterator(Iterator<T> wrapped) {
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

}
