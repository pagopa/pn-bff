package it.pagopa.pn.bff.utils.helpers;

import java.util.ArrayList;
import java.util.List;

public class ArrayHelpers {
    public static <T> ArrayList<T> reverseArray(List<T> array) {
        ArrayList<T> reversedArray = new ArrayList<>();
        for (int i = array.size() - 1; i >= 0; i--) {
            reversedArray.add(array.get(i));
        }
        return reversedArray;
    }
}
