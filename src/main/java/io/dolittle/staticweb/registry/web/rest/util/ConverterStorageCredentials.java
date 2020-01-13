// Copyright (c) Dolittle. All rights reserved.
// Licensed under the MIT license. See LICENSE file in the project root for full license information.

package io.dolittle.staticweb.registry.web.rest.util;

import com.microsoft.azure.storage.StorageCredentials;
import com.microsoft.azure.storage.StorageCredentialsAccountAndKey;
import org.springframework.boot.context.properties.ConfigurationPropertiesBinding;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
@ConfigurationPropertiesBinding
public class ConverterStorageCredentials implements Converter<String, StorageCredentials> {

    @Override
    public StorageCredentials convert(String string) {
        String[] split = string.split(",");
        return new StorageCredentialsAccountAndKey(split[0], split[1]);
    }
}
