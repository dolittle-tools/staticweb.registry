// Copyright (c) Dolittle. All rights reserved.
// Licensed under the MIT license. See LICENSE file in the project root for full license information.

package io.dolittle.staticweb.registry.web.rest.handler;

import lombok.extern.slf4j.Slf4j;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ErrorHandler extends RouteBuilder {

    @Override
    public void configure() {

        from("direct:error-handle")
                .routeId("error-handle")
                .log(LoggingLevel.ERROR, log, "Error message: ${exception.message}")
                .marshal().json().endRest();

    }
}
