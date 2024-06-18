package org.github.drew458.core;

public class Utils {

    protected static Long toLong(String str) {
        return Integer.valueOf(str.hashCode()).longValue();
    }
}
