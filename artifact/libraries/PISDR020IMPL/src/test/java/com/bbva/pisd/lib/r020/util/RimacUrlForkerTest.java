package com.bbva.pisd.lib.r020.util;

import com.bbva.elara.configuration.manager.application.ApplicationConfigurationService;
import com.bbva.pisd.lib.r020.impl.util.RimacUrlForker;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.mockito.Mockito.*;

public class RimacUrlForkerTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(RimacUrlForkerTest.class);
    private final RimacUrlForker rimacUrlForker = new RimacUrlForker();
    private ApplicationConfigurationService applicationConfigurationService;

    @Before
	public void setUp() {

		applicationConfigurationService = mock(ApplicationConfigurationService.class);

		rimacUrlForker.setApplicationConfigurationService(applicationConfigurationService);
        when(this.applicationConfigurationService.getProperty("api.connector.financingplan.rimac.832.url")).thenReturn("https://apitest.rimac.com/api-rrgg/V1/cotizaciones/{idCotizacion}/cronogramas");
        when(this.applicationConfigurationService.getProperty("api.connector.financingplan.rimac.830.url")).thenReturn("https://apitest.rimac.com/vehicular/V1/cotizacion/cronograma/calcular-cuota");
        when(this.applicationConfigurationService.getProperty("api.connector.paymentschedule.rimac.830.url")).thenReturn("https://apitest.rimac.com/vehicular/V1/cotizacion/{idCotizacion}/cronogramapago");
	}

    @Test
    public void rimacUrlForkerTest_OK() {
        LOGGER.info("RimacUrlForkerTest - Executing rimacUrlForkerTest_OK   ");
        rimacUrlForker.generateUriForSignatureAWSQuoteSchedule("830",null);
        rimacUrlForker.generateUriForSignatureAWSQuoteSchedule("832","9a64a5ed-509f-4baa-88e3-a0e373b49e65");
        rimacUrlForker.generatePropertyKeyNameQuoteSchedule("830");
        rimacUrlForker.generatePropertyKeyNameQuoteSchedule("832");
        rimacUrlForker.generateUriForSignatureAWSPaymentSchedule("830","9a64a5ed-509f-4baa-88e3-a0e373b49e65");
        rimacUrlForker.generatePropertyKeyNamePaymentSchedule("830");
    }
}
