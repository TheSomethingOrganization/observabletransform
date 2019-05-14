package io.mrarm.observabletransform;

import androidx.databinding.ObservableArrayList;
import androidx.databinding.ObservableList;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class ObservableListFilterTest {

    private static FilterFunction<String> simpleFiter =
            new FilterFunction< String>() {
                @Override
                public boolean filter(String s) {
                    return s.startsWith("#1");
                }
            };

    private static void checkListWithArray(List<String> s, String[] expected) {
        assertEquals(expected.length, s.size());
        for (int i = 0; i < expected.length; i++)
            assertEquals(expected[i], s.get(i));
    }

    @Test
    public void basicTest() {
        ObservableList<String> test = new ObservableArrayList<>();
        for (int i = 0; i <= 100; i++)
            test.add("#" + i);
        TransformedObservableList<String> transformedTest =
                ObservableLists.filter(test, simpleFiter);
        transformedTest.bind();
        String[] expectedResults = new String[] { "#1", "#10", "#11", "#12", "#13", "#14", "#15", "#16", "#17", "#18", "#19", "#100" };
        checkListWithArray(transformedTest, expectedResults);
        transformedTest.unbind();
    }

    @Test
    public void insertionTest() {
        ObservableList<String> test = new ObservableArrayList<>();
        List<String> missingData = new ArrayList<>();
        for (int i = 0; i <= 100; i++) {
            if (i == 15)
                continue;
            if (i >= 12 && i <= 16)
                missingData.add("#" + i);
            else
                test.add("#" + i);
        }
        TransformedObservableList<String> transformedTest =
                ObservableLists.filter(test, simpleFiter);
        transformedTest.bind();
        String[] expectedResults1 = new String[] { "#1", "#10", "#11", "#17", "#18", "#19", "#100" };
        checkListWithArray(transformedTest, expectedResults1);

        test.addAll(12, missingData);
        String[] expectedResults2 = new String[] { "#1", "#10", "#11", "#12", "#13", "#14", "#16", "#17", "#18", "#19", "#100" };
        checkListWithArray(transformedTest, expectedResults2);

        test.add(15, "#15");
        String[] expectedResults3 = new String[] { "#1", "#10", "#11", "#12", "#13", "#14", "#15", "#16", "#17", "#18", "#19", "#100" };
        checkListWithArray(transformedTest, expectedResults3);
        transformedTest.unbind();
    }

    @Test
    public void removalTest() {
        ObservableList<String> test = new ObservableArrayList<>();
        for (int i = 0; i <= 100; i++)
            test.add("#" + i);
        TransformedObservableList<String> transformedTest =
                ObservableLists.filter(test, simpleFiter);
        transformedTest.bind();
        test.remove(15);
        String[] expectedResults1 = new String[] { "#1", "#10", "#11", "#12", "#13", "#14", "#16", "#17", "#18", "#19", "#100" };
        checkListWithArray(transformedTest, expectedResults1);

        test.subList(12, 16).clear();
        String[] expectedResults2 = new String[] { "#1", "#10", "#11", "#17", "#18", "#19", "#100" };
        checkListWithArray(transformedTest, expectedResults2);
        transformedTest.unbind();
    }

    @Test
    public void updateSingleTest() {
        ObservableList<String> test = new ObservableArrayList<>();
        for (int i = 0; i <= 100; i++)
            test.add("#" + i);
        TransformedObservableList<String> transformedTest =
                ObservableLists.filter(test, simpleFiter);
        transformedTest.bind();

        for (int i = 12; i <= 19; i++) {
            if (i == 18) // test: (was accepted, is accepted)
                continue;
            test.set(i, "--invalid--"); // test: (was accepted, is not accepted)
        }
        String[] expectedResults1 = new String[] { "#1", "#10", "#11", "#18", "#100" };
        checkListWithArray(transformedTest, expectedResults1);

        test.set(11, "--skip--");
        test.set(12, "#12"); // test: (was not accepted, is accepted)
        test.set(13, "--skip--"); // test: (was not accepted, is not accepted)
        test.set(14, "--skip--");
        test.set(15, "#15");
        String[] expectedResults2 = new String[] { "#1", "#10", "#12", "#15", "#18", "#100" };
        checkListWithArray(transformedTest, expectedResults2);

        transformedTest.unbind();
    }

    @Test
    public void updateMultiTest() {
        TestObservableArrayList<String> test = new TestObservableArrayList<>();
        for (int i = 0; i <= 100; i++)
            test.add("#" + i);
        TransformedObservableList<String> transformedTest =
                ObservableLists.filter(test, simpleFiter);
        transformedTest.bind();

        for (int i = 12; i <= 19; i++) {
            if (i == 18) // test: (was accepted, is accepted)
                continue;
            test.set(i, "--invalid--"); // test: (was accepted, is not accepted)
        }
        test.listeners.notifyChanged(test, 12, 19);
        String[] expectedResults1 = new String[] { "#1", "#10", "#11", "#18", "#100" };
        checkListWithArray(transformedTest, expectedResults1);

        test.set(11, "--skip--");
        test.set(12, "#12"); // test: (was not accepted, is accepted)
        test.set(13, "--skip--"); // test: (was not accepted, is not accepted)
        test.set(14, "--skip--");
        test.set(15, "#15");
        test.listeners.notifyChanged(test, 11, 15);
        String[] expectedResults2 = new String[] { "#1", "#10", "#12", "#15", "#18", "#100" };
        checkListWithArray(transformedTest, expectedResults2);

        transformedTest.unbind();
    }

}
