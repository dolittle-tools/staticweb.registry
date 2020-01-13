// Copyright (c) Dolittle. All rights reserved.
// Licensed under the MIT license. See LICENSE file in the project root for full license information.

package io.dolittle.staticweb.registry.web.rest;

import io.dolittle.staticweb.registry.web.model.RestResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.support.processor.validation.PredicateValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class UploadEndpoint extends RouteBuilder {

    @Override
    public void configure() throws Exception {

        onException(PredicateValidationException.class)
            .handled(true)
            .setBody(simple(HttpStatus.BAD_REQUEST.getReasonPhrase(), RestResponse.class))
            .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(HttpStatus.BAD_REQUEST.value()))
            .to("direct:error-handle");

        rest("/upload/")
            .put("/{msId}/{tag}").description("Upload artifact")
            .consumes(MediaType.MULTIPART_FORM_DATA_VALUE)
            .produces(MediaType.APPLICATION_JSON_VALUE)
            .route()
                .routeId("upload-endpoint")
                    .validate(header("Tenant-ID").isNotNull())
            .log(LoggingLevel.INFO, log, "Receiving an upload for Tenant: ${header.Tenant-ID}, appId: ${header.msId}, tag:${header.tag}")
                .process(exchange -> {
                    exchange.setProperty("tenant", exchange.getIn().getHeader("Tenant-ID"));
                    exchange.setProperty("ms", exchange.getIn().getHeader("msId"));
                    exchange.setProperty("tag", exchange.getIn().getHeader("tag"));
                })

            .to("direct:upload-check")
                .log(LoggingLevel.INFO, log, "Upload complete.")
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(HttpStatus.OK.value()))
                .setBody(simple("Content uploaded", RestResponse.class))
                .marshal().json()
        .endRest();

    }

}
