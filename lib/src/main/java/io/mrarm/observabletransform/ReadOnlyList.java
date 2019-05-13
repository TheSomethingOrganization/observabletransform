package io.mrarm.observabletransform;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.function.UnaryOperator;

public abstract class ReadOnlyList<T> implements List<T> {

    @Override
    public boolean add(T t) {
        throw new UnsupportedOperationException("List is read-only");
    }

    @Override
    public boolean remove(@Nullable Object o) {
        throw new UnsupportedOperationException("List is read-only");
    }

    @Override
    public boolean addAll(@NonNull Collection<? extends T> c) {
        throw new UnsupportedOperationException("List is read-only");
    }

    @Override
    public boolean addAll(int index, @NonNull Collection<? extends T> c) {
        throw new UnsupportedOperationException("List is read-only");
    }

    @Override
    public boolean removeAll(@NonNull Collection<?> c) {
        throw new UnsupportedOperationException("List is read-only");
    }

    @Override
    public boolean retainAll(@NonNull Collection<?> c) {
        throw new UnsupportedOperationException("List is read-only");
    }

    @Override
    public void replaceAll(@NonNull UnaryOperator<T> operator) {
        throw new UnsupportedOperationException("List is read-only");
    }

    @Override
    public void sort(@Nullable Comparator<? super T> c) {
        throw new UnsupportedOperationException("List is read-only");
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException("List is read-only");
    }

    @Override
    public T set(int index, T element) {
        throw new UnsupportedOperationException("List is read-only");
    }

    @Override
    public void add(int index, T element) {
        throw new UnsupportedOperationException("List is read-only");
    }

    @Override
    public T remove(int index) {
        throw new UnsupportedOperationException("List is read-only");
    }

}
