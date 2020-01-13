// Copyright (c) Dolittle. All rights reserved.
// Licensed under the MIT license. See LICENSE file in the project root for full license information.

package io.dolittle.staticweb.registry.web.rest.handler;

import com.microsoft.azure.storage.StorageException;
import io.dolittle.staticweb.registry.web.model.Manifest;
import io.dolittle.staticweb.registry.web.model.RestResponse;
import io.dolittle.staticweb.registry.web.rest.util.Util;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.azure.blob.BlobBlock;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

@Component
@Slf4j
public class AzureBlobHandler extends RouteBuilder {

    private final Environment environment;

    public AzureBlobHandler(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void configure() throws Exception {

        String accountName = environment.getProperty("camel.component.azure-blob.configuration.account-name");
        String containerName = environment.getProperty("camel.component.azure-blob.configuration.container-name");

        onException(Exception.class)
                .handled(true)
                .setBody(simple(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase() + ": Please try again later.", RestResponse.class))
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(HttpStatus.INTERNAL_SERVER_ERROR.value()))
                .to("direct:error-handle");

        from("direct:artifact-write")
                .routeId("artifact-write")
                .process(exchange -> {
                    InputStream body = exchange.getIn().getBody(InputStream.class);
                    BlobBlock blobBlock = new BlobBlock(body);
                    exchange.getIn().setBody(blobBlock);
                })
                .toD("azure-blob://" + accountName + "/" + containerName + "/" + "${exchangeProperty.tenant}/${exchangeProperty.ms}/${exchangeProperty.sha}.zip?operation=uploadBlobBlocks")
                .log(LoggingLevel.INFO, log, "Artifact uploaded to container")
                .to("direct:manifest-write")
                .end();

        from("direct:manifest-read")
                .routeId("manifest-read")

                .onException(StorageException.class)
                .process(exchange -> {
                    log.info("Creating new manifest");
                    Manifest manifest = new Manifest();
                    manifest.setTenantId(exchange.getProperty("tenant", String.class));
                    manifest.setMsId(exchange.getProperty("ms", String.class));
                    exchange.getIn().setBody(manifest);
                }).handled(true).end()

                .toD("azure-blob://" + accountName + "/" + containerName + "/" + "${exchangeProperty.tenant}/${exchangeProperty.ms}" + "/manifest.json?operation=getBlob")
                .unmarshal().json(JsonLibrary.Jackson, Manifest.class)
                .log(LoggingLevel.INFO, log, "Got a manifest ...")
                .end();

        from("direct:manifest-write")
                .routeId("manifest-write")
                .process(exchange -> {
                    exchange.getIn().setBody(Util.getManifest(exchange));
                }).marshal().json()
                .process(exchange -> {
                    InputStream body = exchange.getIn().getBody(ByteArrayInputStream.class);
                    BlobBlock blobBlock = new BlobBlock(body);
                    exchange.getIn().setBody(blobBlock);
                })
                .toD("azure-blob://" + accountName + "/" + containerName + "/" + "${exchangeProperty.tenant}/${exchangeProperty.ms}" + "/manifest.json?operation=uploadBlobBlocks")
                .log(LoggingLevel.INFO,log, "Manifest written")
                .end();

    }


}
