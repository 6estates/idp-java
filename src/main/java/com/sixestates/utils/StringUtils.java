package com.sixestates.utils;

/**
 * @author kechen, 06/08/24.
 */
public class StringUtils {
    public static Boolean isNotEmpty(String str){
        if (str == null || str.length() == 0) {
            return false;
        }
        return true;
    }

}
