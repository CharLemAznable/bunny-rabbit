package com.github.charlemaznable.bunny.rabbit.spring;

import com.github.charlemaznable.bunny.client.spring.BunnyEventBusImport;
import com.github.charlemaznable.bunny.client.spring.BunnyOhClientImport;
import com.github.charlemaznable.bunny.rabbit.handler.BunnyHandlerScanAnchor;
import com.github.charlemaznable.core.spring.ComplexComponentScan;
import com.github.charlemaznable.core.vertx.spring.SpringVertxImport;
import org.n3r.eql.eqler.spring.EqlerScan;

@ComplexComponentScan(basePackageClasses = {
        BunnyComponents.class,
        BunnyHandlerScanAnchor.class})
@EqlerScan(basePackageClasses = {
        BunnyHandlerScanAnchor.class})
@SpringVertxImport
@BunnyOhClientImport
@BunnyEventBusImport
public final class BunnyComponents {
}
