package com.sixestates.utils;


import java.util.Collection;

/**
 * @author kechen, 06/08/24.
 */
public class CollectionUtils {
    public static boolean isEmpty(Collection<?> collection) {
        return (collection == null || collection.isEmpty());
    }
}
