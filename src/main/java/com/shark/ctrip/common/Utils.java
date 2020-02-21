package com.shark.ctrip.common;

import java.util.Arrays;
import java.util.List;

public class Utils {
    private static final List<Class<?>> PRI_TYPE = Arrays.asList(String.class, Integer.class, Long.class, Boolean.class);

    public static String toBigCamelCase(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    public static String toLowCamelCase(String str) {
        return str.substring(0, 1).toLowerCase() + str.substring(1);
    }

    public static boolean isPrimitiveType(Object o) {
        return isPrimitiveType(o.getClass());
    }

    public static boolean isPrimitiveType(Class<?> c) {
        return PRI_TYPE.contains(c);
    }

    public static String getType(String type, boolean isArray, String name) {
        String fmt = "%s";
        if (isArray) {
            fmt = "java.util.List<%s>";
        }
        switch (type) {
            case "string":
                return String.format(fmt, "String");
            case "boolean":
                return String.format(fmt, "Boolean");
            case "integer":
                return String.format(fmt, "Integer");
            case "number":
                return String.format(fmt, "Long");
            default:
                return String.format(fmt, toBigCamelCase(name));
        }
    }
}
