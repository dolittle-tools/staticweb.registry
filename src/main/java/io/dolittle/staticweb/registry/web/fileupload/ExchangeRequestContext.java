// Copyright (c) Dolittle. All rights reserved.
// Licensed under the MIT license. See LICENSE file in the project root for full license information.

package io.dolittle.staticweb.registry.web.fileupload;

import org.apache.camel.Exchange;
import org.apache.commons.fileupload.RequestContext;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class ExchangeRequestContext implements RequestContext {
    private Exchange exchange;

    public ExchangeRequestContext(Exchange exchange) {
        this.exchange = exchange;
    }

    @Override
    public String getCharacterEncoding() {
        return StandardCharsets.UTF_8.toString();
    }

    @Override
    public String getContentType() {
        return exchange.getIn().getHeader("Content-Type").toString();
    }

    @Override
    public int getContentLength() {
        return -1;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return this.exchange.getIn().getBody(InputStream.class);
    }
}
