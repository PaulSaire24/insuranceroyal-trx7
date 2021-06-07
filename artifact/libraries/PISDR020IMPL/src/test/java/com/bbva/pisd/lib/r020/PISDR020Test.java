package com.bbva.pisd.lib.r020;

import com.bbva.elara.configuration.manager.application.ApplicationConfigurationService;
import com.bbva.elara.domain.transaction.Context;
import com.bbva.elara.domain.transaction.ThreadContext;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.Advised;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
		"classpath:/META-INF/spring/PISDR020-app.xml",
		"classpath:/META-INF/spring/PISDR020-app-test.xml",
		"classpath:/META-INF/spring/PISDR020-arc.xml",
		"classpath:/META-INF/spring/PISDR020-arc-test.xml" })
public class PISDR020Test {

	private static final Logger LOGGER = LoggerFactory.getLogger(PISDR020Test.class);


	@Spy
	private Context context;

	@Test
	public void executeTest(){
		LOGGER.info("Executing the test...");
	}

}
