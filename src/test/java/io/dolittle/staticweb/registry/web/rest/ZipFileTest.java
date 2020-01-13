// Copyright (c) Dolittle. All rights reserved.
// Licensed under the MIT license. See LICENSE file in the project root for full license information.

package io.dolittle.staticweb.registry.web.rest;

import io.dolittle.staticweb.registry.web.rest.util.Util;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.io.InputStream;

@RunWith(SpringRunner.class)
public class ZipFileTest {


    private final String EMPTY_ZIP_FILE = "files/empty.zip";
    private final String TEXT_FILE = "files/test.txt";

    @Test
    public void verifyFileIsZip() {
        try {
            InputStream inputStream = new ClassPathResource(EMPTY_ZIP_FILE).getInputStream();
            Boolean isZipFile = Util.isZipFile(inputStream);
            Assert.assertTrue(isZipFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void verifyFileIsNotZip() {
        try {
            InputStream inputStream = new ClassPathResource(TEXT_FILE).getInputStream();
            Boolean isZipFile = Util.isZipFile(inputStream);
            Assert.assertFalse(isZipFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void verifyZipIsEmpty() {
        try {
            InputStream inputStream = new ClassPathResource(EMPTY_ZIP_FILE).getInputStream();
            Boolean isZipFile = Util.isZipFileEmpty(inputStream);
            Assert.assertTrue(isZipFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
