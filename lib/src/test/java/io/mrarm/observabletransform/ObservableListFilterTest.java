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

}
