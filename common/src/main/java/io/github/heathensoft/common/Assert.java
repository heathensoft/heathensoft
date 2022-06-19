package io.github.heathensoft.common;

/**
 * @author Frederik Dahl
 * 15/06/2022
 */


public class Assert {
    
    private static final String DEFAULT_MESSAGE = "ASSERTION FAILED";
    
    public static void notNull(Object o) {
        if (o == null) {
            Print.out(DEFAULT_MESSAGE);
            throw new RuntimeException();
        }
    }
    
    public static void notNull(Object... o) {
        if (o == null) {
            Print.out(DEFAULT_MESSAGE);
            throw new RuntimeException();
        }
    }
    
    public static void isNull(Object o) {
        if (o != null) {
            Print.out(DEFAULT_MESSAGE);
            throw new RuntimeException();
        }
    }
    
    public static void isClass(Object o, Class<?> clazz) {
        if (o == null || o.getClass() != clazz) {
            Print.out(DEFAULT_MESSAGE);
            throw new RuntimeException();
        }
    }
    
    
}
