// Copyright (c) Dolittle. All rights reserved.
// Licensed under the MIT license. See LICENSE file in the project root for full license information.

package io.dolittle.staticweb.registry.web.rest;

import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import io.dolittle.staticweb.registry.web.model.Content;
import io.dolittle.staticweb.registry.web.model.Manifest;
import io.dolittle.staticweb.registry.web.model.Tag;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.IOException;

@RunWith(SpringRunner.class)
public class ManifestFileTest {
    private final String MANIFEST_FILE = "src/test/resources/files/manifest.json";
    private final ObjectMapper mapper = new ObjectMapper();
    private final String DEFAULT_TAG = "1.0";
    private final String DEFAULT_CONTENT = "a1b2c3s4";

    @Test
    public void checkTag() {
        Manifest manifest = getManifest();

        Assert.assertTrue(manifest.hasTag(DEFAULT_TAG));
        Assert.assertFalse(manifest.hasTag(DEFAULT_TAG + ".1"));
    }

    @Test
    public void checkContent() {
        Manifest manifest = getManifest();

        Assert.assertTrue(manifest.hasContent(DEFAULT_CONTENT));
        Assert.assertFalse(manifest.hasContent(DEFAULT_CONTENT +"5"));
    }

    @Before
    public void createManifest() {
        Manifest manifest = new Manifest();
        manifest.setMsId("ms-1234");
        manifest.setTenantId("tenant-5678");
        manifest.addTag(DEFAULT_TAG, new Tag(DEFAULT_CONTENT));
        manifest.addContent(DEFAULT_CONTENT, new Content(DEFAULT_CONTENT + ".zip"));
        ObjectWriter writer = mapper.writer(new DefaultPrettyPrinter());
        try {
            System.out.println("Creating new manifest file");
            File file = new File(MANIFEST_FILE);
            boolean created = file.createNewFile();
            if (created) {
                writer.writeValue(file, manifest);
            } else throw new IOException("Unable to create a manifest file");

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public Manifest getManifest() {
        Manifest manifest = null;
        try {
            manifest = mapper.readValue(new File(MANIFEST_FILE), Manifest.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return manifest;
    }

    @After
    public void deleteManifest() {
        File file = new File(MANIFEST_FILE);
        boolean deleted = file.delete();
        if (deleted) {
            System.out.println("File deleted");
        } else System.out.println("Unable to clean up after test");

    }
}
