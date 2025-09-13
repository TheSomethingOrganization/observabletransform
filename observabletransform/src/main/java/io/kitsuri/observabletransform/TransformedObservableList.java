package io.kitsuri.observabletransform;

import androidx.databinding.ObservableList;

/**
 * This interface specifies an observable list which has been transformed by one of the methods.
 *
 * In particular it defines two methods: bind() and unbind(). The methods allow you to dispose of
 * the list correctly. If unbind() is not called then the list will stay around in memory consuming
 * both memory and CPU until the parent list has been disposed.
 *
 * The bind() and unbind() methods should be reference counted. That is, if bind() is called twice,
 * then for the list to actually unbind() it should also be called twice.
 * A bind() on a child list should also call bind() on all parent lists (same applies to unbind())
 */
public interface TransformedObservableList<T> extends ObservableList<T>, Bindable {

    /**
     * Forcibly reapplies the transform to all the source items.
     */
    void reapply();

    /**
     * Reapplies the transform to the specified item range in the source list.
     * @param startPosition range start position
     * @param itemCount count of items from the start to reapply the filter to
     */
    void reapply(int startPosition, int itemCount);

}
