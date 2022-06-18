package io.github.heathensoft.common;

/**
 * @author Frederik Dahl
 * 15/06/2022
 */


public class Assert {
    
    public static void notNull(Object o) {
        if (o == null) throw new RuntimeException();
    }
    
    public static void notNull(Object... o) {
        if (o == null) throw new RuntimeException();
    }
    
    public static void isNull(Object o) {
        if (o != null) throw new RuntimeException();
    }
    
    public static void isClass(Object o, Class<?> clazz) {
        if (o == null || o.getClass() != clazz) throw new RuntimeException();
    }
    
    
}
