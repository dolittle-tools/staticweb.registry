// Copyright (c) Dolittle. All rights reserved.
// Licensed under the MIT license. See LICENSE file in the project root for full license information.

package io.dolittle.staticweb.registry.web.model;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class Manifest {
    private String tenantId;
    private String msId;
    private Map<String, Tag> tags = new HashMap<>();
    private Map<String, Content> contents = new HashMap<>();

    public void addTag(String key, Tag tag) {
        this.tags.put(key, tag);
    }

    public void addContent(String key, Content content) {
        this.contents.put(key, content);
    }

    public Boolean hasContent(String content) {
        if (contents == null) return false;
        return this.contents.containsKey(content);
    }

    public Boolean hasTag(String tag) {
        if (tag == null) return false;
        return this.tags.containsKey(tag);
    }

}
