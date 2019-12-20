// Copyright (c) Dolittle. All rights reserved.
// Licensed under the MIT license. See LICENSE file in the project root for full license information.

package io.dolittle.staticweb.registry.web;

import io.dolittle.staticweb.registry.config.web.AppTestConfig;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {AppTestConfig.class})
@Slf4j
public class SPARegistryApplicationTests {

	@Test
	public void contextLoads() {
	  log.info("Hello Test");
	}

}
