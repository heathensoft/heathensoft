package io.github.heathensoft.common;

/**
 * @author Frederik Dahl
 * 16/06/2022
 */


public enum OS {
    
    WINDOWS, LINUX, MAC, SOLARIS, UNDEFINED;
    
    private static OS os = null;
    
    public static OS get() {
        if (os == null) {
            String system = System.getProperty("os.name").toLowerCase();
            if (system.contains("win")) {
                os = OS.WINDOWS;
            } else if (system.contains("nix") || system.contains("nux") || system.contains("aix")) {
                os = LINUX;
            } else if (system.contains("mac")) {
                os = MAC;
            } else if (system.contains("sunos")) {
                os = SOLARIS;
            } else os = UNDEFINED;
        } return os;
    }
    
}
