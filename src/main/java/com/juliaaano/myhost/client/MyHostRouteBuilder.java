package com.juliaaano.myhost.client;

import org.apache.camel.builder.RouteBuilder;

import java.util.Map;

import static org.apache.camel.model.dataformat.JsonLibrary.Gson;

public class MyHostRouteBuilder extends RouteBuilder {

    @Override
    public void configure() {

        from("timer:prime?period=2000")
                .routeId("myhost-polling")
                .to("http:{{service:juliaaano-myhost:localhost:8000}}/myhost")
                .unmarshal().json(Gson, Map.class)
                .log("Hello from host ${body[localHostName]}");
    }
}
