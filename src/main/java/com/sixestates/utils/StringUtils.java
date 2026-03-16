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

    public static boolean equals(String str1, String str2) {
        return str1 == null ? str2 == null : str1.equals(str2);
    }

}
