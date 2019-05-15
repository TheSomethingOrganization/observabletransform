package io.mrarm.observabletransform;

import androidx.databinding.Observable;
import androidx.databinding.ObservableArrayList;
import androidx.databinding.ObservableList;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ObservableListSizeTest {

    @Test
    public void basicTest() {
        ObservableList<String> test = new ObservableArrayList<>();
        final BindableObservableInt size = ObservableLists.size(test);
        final SettableField<Integer> size2 = new SettableField<>();
        size.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                size2.field = size.get();
            }
        });
        for (int i = 0; i < 10; i++)
            test.add("#" + i);
        size.bind();
        assertEquals(test.size(), (int) size2.field);
        for (int i = 10; i < 20; i++)
            test.add("#" + i);
        assertEquals(test.size(), (int) size2.field);
    }

    private static class SettableField<T> {
        private T field;
    }

}