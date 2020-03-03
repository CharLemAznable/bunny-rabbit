package com.github.charlemaznable.bunny.rabbittest.vertx;

import com.github.charlemaznable.bunny.rabbit.vertx.BunnyApplication;
import com.github.charlemaznable.bunny.rabbit.vertx.common.BunnyHandler;
import com.github.charlemaznable.core.lang.Listt;
import lombok.var;
import org.junit.jupiter.api.Test;

import java.util.Collection;

import static com.github.charlemaznable.core.lang.Listt.newArrayList;
import static org.joor.Reflect.on;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ConstructTest {

    @SuppressWarnings("RedundantCast")
    @Test
    public void testConstruct() {
        var application = new BunnyApplication();
        var reflect = on(application);
        assertNull(reflect.get("eventBusConfig"));
        assertNull(reflect.get("httpServerConfig"));
        assertTrue(((Collection) reflect.get("handlers")).isEmpty());

        application = new BunnyApplication(
                Listt.<BunnyHandler<?, ?>>newArrayList().iterator());
        reflect = on(application);
        assertNull(reflect.get("eventBusConfig"));
        assertNull(reflect.get("httpServerConfig"));
        assertTrue(((Collection) reflect.get("handlers")).isEmpty());

        application = new BunnyApplication(newArrayList());
        reflect = on(application);
        assertNull(reflect.get("eventBusConfig"));
        assertNull(reflect.get("httpServerConfig"));
        assertTrue(((Collection) reflect.get("handlers")).isEmpty());

        application = new BunnyApplication(
                new BunnyHandler<?, ?>[0], handler -> handler);
        reflect = on(application);
        assertNull(reflect.get("eventBusConfig"));
        assertNull(reflect.get("httpServerConfig"));
        assertTrue(((Collection) reflect.get("handlers")).isEmpty());

        application = new BunnyApplication(
                Listt.<BunnyHandler<?, ?>>newArrayList().iterator(), handler -> handler);
        reflect = on(application);
        assertNull(reflect.get("eventBusConfig"));
        assertNull(reflect.get("httpServerConfig"));
        assertTrue(((Collection) reflect.get("handlers")).isEmpty());

        application = new BunnyApplication(
                Listt.<BunnyHandler<?, ?>>newArrayList(), handler -> handler);
        reflect = on(application);
        assertNull(reflect.get("eventBusConfig"));
        assertNull(reflect.get("httpServerConfig"));
        assertTrue(((Collection) reflect.get("handlers")).isEmpty());
    }
}
