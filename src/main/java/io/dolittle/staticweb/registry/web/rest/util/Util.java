// Copyright (c) Dolittle. All rights reserved.
// Licensed under the MIT license. See LICENSE file in the project root for full license information.

package io.dolittle.staticweb.registry.web.rest.util;

import io.dolittle.staticweb.registry.web.model.Manifest;
import org.apache.camel.Exchange;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.RandomStringUtils;

import java.io.*;
import java.util.zip.ZipFile;

public class Util {

    /**
     * Checking signature of file to determine if its zip
     50 4B 03 04
     50 4B 05 06 (empty archive)
     50 4B 07 08 (spanned archive)
     */
    public static Boolean isZipFile(InputStream is) throws IOException {
        DataInputStream dataInputStream = new DataInputStream(is);
        int fileSignature = dataInputStream.readInt();
        dataInputStream.close();
        return fileSignature == 0x504B0304 || fileSignature == 0x504B0506 || fileSignature == 0x504B0708;
    }

    public static String GetSHA256(byte[] is) throws IOException {
        return DigestUtils.sha256Hex(is);
    }

    public static Boolean isZipFileEmpty(InputStream is) throws IOException {
        File tempFile = File.createTempFile(RandomStringUtils.random(6), "zip");
        FileOutputStream out = new FileOutputStream(tempFile);
        IOUtils.copy(is, out);
        out.close();

        ZipFile zipFile = new ZipFile(tempFile);
        int size = zipFile.size();
        boolean deleted = tempFile.delete();
        return size == 0;
    }

    public static Manifest getManifest(Exchange exchange) {
        return exchange.getProperty("manifest", Manifest.class);
    }

    public static String getTag(Exchange exchange) {
        return exchange.getProperty("tag", String.class);
    }
}
