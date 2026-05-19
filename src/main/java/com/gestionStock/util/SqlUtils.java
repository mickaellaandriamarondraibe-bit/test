package com.gestionStock.util;

public final class SqlUtils {

    private SqlUtils() {
    }

    public static String toSnakeCase(String value) {
        return value
                .replaceAll("([a-z])([A-Z])", "$1_$2")
                .toLowerCase();
    }

    public static String tableName(Class<?> clazz) {
        return toSnakeCase(clazz.getSimpleName());
    }
}
