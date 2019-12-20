// Copyright (c) Dolittle. All rights reserved.
// Licensed under the MIT license. See LICENSE file in the project root for full license information.

package io.dolittle.staticweb.registry.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"io.dolittle.staticweb.registry.config.web"})
@Slf4j
public class SPARegistryApplication {

	public static void main(String[] args) {
		SpringApplication.run(SPARegistryApplication.class, args);
		log.info("************ SPA Registry STARTED **************");
	}

}
