package com.example.employee.common;

public final class Constant {

    private Constant() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static final String START = "===================================== START =====================================";

    public static final String END = "=====================================  END  =====================================";

    public static final String LOCAL_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";

    public static final String DATETIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX";

    public static final String CONFIG_FILE_FORMAT = "config-%s%s.properties";

    public static final String DATE_FORMAT = "yyyy-MM-dd";

    public static final String FULL_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    public static final String MESSAGE_NOT_FOUND = "Resource not found";

    public static final String CUSTOM_DATETIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";

}
