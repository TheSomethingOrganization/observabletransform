package io.mrarm.observabletransform;

import androidx.databinding.ObservableArrayList;
import androidx.databinding.ObservableList;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class ObservableListTransformTest {

    private static TransformFunction<String, String> simpleTransform =
            new TransformFunction<String, String>() {
                @Override
                public String transform(String s) {
                    return "Test " + s;
                }
            };

    @Test
    public void basicTest() {
        ObservableList<String> test = new ObservableArrayList<>();
        for (int i = 0; i < 100; i++)
            test.add("#" + i);
        TransformedObservableList<String> transformedTest =
                ObservableLists.transform(test, simpleTransform);
        transformedTest.bind();
        assertEquals(100, transformedTest.size());
        for (int i = 0; i < 100; i++)
            assertEquals("Test #" + i, transformedTest.get(i));
        transformedTest.unbind();
    }

    private static void checkMatch(List<String> a, List<String> b,
                                   TransformFunction<String, String> t) {
        assertEquals(a.size(), b.size());
        for (int i = 0; i < a.size(); i++)
            assertEquals(t.transform(a.get(i)), b.get(i));
    }

    @Test
    public void insertionTest() {
        ObservableList<String> test = new ObservableArrayList<>();
        for (int i = 0; i < 100; i++)
            test.add("#" + i);
        TransformedObservableList<String> transformedTest =
                ObservableLists.transform(test, simpleTransform);
        transformedTest.bind();
        checkMatch(test, transformedTest, simpleTransform);
        test.add(10, "-- Test --");
        checkMatch(test, transformedTest, simpleTransform);
        test.addAll(25, Arrays.asList("a", "b", "c", "d"));
        checkMatch(test, transformedTest, simpleTransform);
        transformedTest.unbind();
    }

    @Test
    public void change() {
        ObservableList<String> test = new ObservableArrayList<>();
        for (int i = 0; i < 100; i++)
            test.add("#" + i);
        TransformedObservableList<String> transformedTest =
                ObservableLists.transform(test, simpleTransform);
        transformedTest.bind();
        checkMatch(test, transformedTest, simpleTransform);
        for (int i = 10; i < 50; i++)
            test.set(i, "##" + i);
        checkMatch(test, transformedTest, simpleTransform);
        transformedTest.unbind();
    }

    @Test
    public void deletionTest() {
        ObservableList<String> test = new ObservableArrayList<>();
        for (int i = 0; i < 100; i++)
            test.add("#" + i);
        TransformedObservableList<String> transformedTest =
                ObservableLists.transform(test, simpleTransform);
        transformedTest.bind();
        checkMatch(test, transformedTest, simpleTransform);
        test.subList(10, 50).clear();
        checkMatch(test, transformedTest, simpleTransform);
        transformedTest.unbind();
    }

}