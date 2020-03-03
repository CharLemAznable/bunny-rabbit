package com.github.charlemaznable.bunny.rabbittest.vertx;

public class TestContext {

    private static ThreadLocal<String> local = new InheritableThreadLocal<>();

    public static String getContext() {
        return local.get();
    }

    public static void setContext(String context) {
        local.set(context);
    }

    public static void clear() {
        local.remove();
    }
}
