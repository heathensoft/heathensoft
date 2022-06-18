package io.github.heathensoft.common;

/**
 * @author Frederik Dahl
 * 18/06/2022
 */


public class Print {
    
    private static final Object lock = new Object();
    private static String currentClassName = "";
    private static long currentThreadID = -1;
    
    public static void out(String string) {
        synchronized (lock) {
            StackWalker stackWalker = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE);
            String callerClass = stackWalker.getCallerClass().getSimpleName();
            if (!currentClassName.equals(callerClass)) {
                currentClassName = callerClass;
                System.out.println("-------------------------------------------------------------------");
                System.out.println(currentClassName);
                System.out.println("-------------------------------------------------------------------");
            } Thread currentThread = Thread.currentThread();
            if (currentThreadID != currentThread.getId()) {
                String threadName = Thread.currentThread().getName();
                System.out.println("                                                                    ["+ threadName +"]");
                currentThreadID = currentThread.getId();
            } System.out.println("\t-- " + string);
        }
    }
    
}
