package com.sixestates.utils;

import java.util.ArrayList;
import java.util.Collections;

/**
 * @author kechen, 06/08/24.
 */
public class Lists {
    public static <T> ArrayList<T> newArrayList(T... elements) {
        if (elements == null) {
            return null;
        }
        ArrayList<T> list = new ArrayList<>();
        Collections.addAll(list, elements);
        return list;
    }

}
