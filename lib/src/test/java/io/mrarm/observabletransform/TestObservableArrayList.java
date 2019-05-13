package io.mrarm.observabletransform;

import androidx.databinding.ListChangeRegistry;
import androidx.databinding.ObservableList;

import java.util.ArrayList;

public class TestObservableArrayList<T> extends ArrayList<T> implements ObservableList<T> {

    public final ListChangeRegistry listeners = new ListChangeRegistry();

    @Override
    public void addOnListChangedCallback(
            OnListChangedCallback<? extends ObservableList<T>> callback) {
        listeners.add(callback);
    }

    @Override
    public void removeOnListChangedCallback(
            OnListChangedCallback<? extends ObservableList<T>> callback) {
        listeners.remove(callback);
    }

}
