package io.github.heathensoft.core.window;

/**
 * @author Frederik Dahl
 * 16/06/2022
 */


public interface WinConfig {
    
    default String title() { return "Heathensoft"; }
    default int targetResolutionWidth() { return 1280; }
    default int targetResolutionHeight() { return 720; }
    default boolean windowedMode() { return true; }
    default boolean antialiasing() { return false; }
    default boolean borderBoxed() { return false; }
    default boolean vsync() { return true; }
}
