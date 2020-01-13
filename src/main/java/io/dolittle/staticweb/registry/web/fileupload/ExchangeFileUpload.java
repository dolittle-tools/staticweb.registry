// Copyright (c) Dolittle. All rights reserved.
// Licensed under the MIT license. See LICENSE file in the project root for full license information.

package io.dolittle.staticweb.registry.web.fileupload;

import org.apache.camel.Exchange;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUpload;
import org.apache.commons.fileupload.FileUploadException;

import java.util.List;

public class ExchangeFileUpload extends FileUpload {
    public ExchangeFileUpload(FileItemFactory fileItemFactory) {
        super(fileItemFactory);
    }

    public List<FileItem> parseExchange(Exchange exchange) throws FileUploadException {
        return parseRequest(new ExchangeRequestContext(exchange));
    }
}
