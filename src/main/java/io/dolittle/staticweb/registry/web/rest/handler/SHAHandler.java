// Copyright (c) Dolittle. All rights reserved.
// Licensed under the MIT license. See LICENSE file in the project root for full license information.

package io.dolittle.staticweb.registry.web.rest.handler;

import io.dolittle.staticweb.registry.web.model.Content;
import io.dolittle.staticweb.registry.web.model.Manifest;
import io.dolittle.staticweb.registry.web.model.Tag;
import io.dolittle.staticweb.registry.web.rest.util.Util;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Component;

import java.io.InputStream;

@Component
@Slf4j
public class SHAHandler extends RouteBuilder {
    @Override
    public void configure() throws Exception {

        from("direct:sha-generate").routeId("sha-generate")

                .log(LoggingLevel.DEBUG, log, "Generate SHA for : ${header.CamelFileName}")
                .process(exchange -> {
                    byte[] byteArray = IOUtils.toByteArray(exchange.getIn().getBody(InputStream.class));
                    log.debug("File size: {}", byteArray.length);

                    String sha256 = Util.GetSHA256(byteArray);
                    exchange.setProperty("sha", sha256);

                    Manifest manifest = Util.getManifest(exchange);
                    manifest.addTag(Util.getTag(exchange), new Tag(sha256));

                    exchange.setProperty("hasContent",manifest.hasContent(sha256));
                    exchange.getIn().getBody(InputStream.class).reset();
                })
                .choice()
                    .when()
                        .simple("${exchangeProperty.hasContent} == 'true'").to("direct:manifest-write")
                    .otherwise()
                        .process(exchange -> {
                            Manifest manifest = Util.getManifest(exchange);
                            String contentPath = getContentPath(exchange);
                            manifest.addContent(exchange.getProperty("sha", String.class), new Content(contentPath));
                        })
                        .to("direct:artifact-write")
                .end()
                .log(LoggingLevel.DEBUG, log, "SHA generated file: ${header.CamelFileName}, SHA: ${exchangeProperty.sha}")
        .end();
    }

    private String getContentPath(Exchange exchange) {
        return "/" + exchange.getProperty("tenant", String.class) +
                "/" + exchange.getProperty("ms", String.class) +
                "/" + exchange.getProperty("sha", String.class) +
                ".zip";
    }
}
