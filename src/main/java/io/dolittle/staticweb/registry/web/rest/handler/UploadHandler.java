// Copyright (c) Dolittle. All rights reserved.
// Licensed under the MIT license. See LICENSE file in the project root for full license information.

package io.dolittle.staticweb.registry.web.rest.handler;

import io.dolittle.staticweb.registry.web.exception.EmptyZipException;
import io.dolittle.staticweb.registry.web.exception.NotZipException;
import io.dolittle.staticweb.registry.web.exception.TagExistException;
import io.dolittle.staticweb.registry.web.fileupload.ExchangeFileUpload;
import io.dolittle.staticweb.registry.web.model.Manifest;
import io.dolittle.staticweb.registry.web.model.RestResponse;
import io.dolittle.staticweb.registry.web.rest.util.Util;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.AggregationStrategy;
import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.List;

@Component
@Slf4j
public class UploadHandler extends RouteBuilder {
    @Override
    public void configure() throws Exception {

        from("direct:upload-check")
            .routeId("upload-check")
                .onException(NotZipException.class)
                    .handled(true)
                    .setBody(simple("File is not a ZIP archive", RestResponse.class))
                    .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(HttpStatus.NOT_ACCEPTABLE.value()))
                    .to("direct:error-handle")
                .end()

                .onException(EmptyZipException.class)
                    .handled(true)
                    .setBody(simple("ZIP archive is empty", RestResponse.class))
                    .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(HttpStatus.NOT_ACCEPTABLE.value()))
                    .to("direct:error-handle")
                .end()

                .onException(TagExistException.class)
                    .handled(true)
                    .setBody(simple("${exception.message}", RestResponse.class))
                    .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(HttpStatus.NOT_ACCEPTABLE.value()))
                    .to("direct:error-handle")
                .end()

                .enrich("direct:manifest-read", (AggregationStrategy) (oldExchange, newExchange) -> {
                    Manifest manifest = newExchange.getIn().getBody(Manifest.class);
                    oldExchange.setProperty("manifest", manifest);
                    return oldExchange;
                })
            .process(exchange -> {

                List<FileItem> items = getFileItems(exchange);

                if(items.size() >= 1){
                    FileItem fileItem = items.get(0);

                    if (!Util.isZipFile(fileItem.getInputStream())) {
                        throw new NotZipException("File is not a ZIP archive: " + fileItem.getName());
                    }

                   if (Util.isZipFileEmpty(fileItem.getInputStream())) {
                       throw new EmptyZipException("Zip file is empty: " + fileItem.getName());
                   }

                    byte[] file = fileItem.getInputStream().readAllBytes();

                    exchange.getIn().setBody(new ByteArrayInputStream(file));
                    exchange.getIn().setHeader(Exchange.FILE_NAME, fileItem.getName());

                    for (int i = 1; i < items.size(); i++) {
                        exchange.setProperty(items.get(i).getName(), items.get(i).getInputStream());
                    }
                }
            })
            .process(exchange -> {
                Manifest manifest = Util.getManifest(exchange);
                String tag = Util.getTag(exchange);

                if (manifest.hasTag(tag)) {
                    log.info("Tag:{} already exists.", tag);
                    throw new TagExistException("Tag: " + tag + " already has content");
                }

            })
            .log(LoggingLevel.DEBUG,log,"Received file: ${header.CamelFileName}")
            .to("direct:sha-generate")
        .end();
    }

    private List<FileItem> getFileItems(Exchange exchange) throws FileUploadException {
        DiskFileItemFactory factory = new DiskFileItemFactory();
        factory.setRepository(new File(System.getProperty("java.io.tmpdir")));
        factory.setSizeThreshold(-1); //We want every file to be stored on disk temporarily
        ExchangeFileUpload upload = new ExchangeFileUpload(factory);
        return upload.parseExchange(exchange);
    }
}
