package com.bbva.pisd.lib.r020;

import com.bbva.elara.configuration.manager.application.ApplicationConfigurationService;
import com.bbva.elara.domain.transaction.Context;
import com.bbva.elara.domain.transaction.ThreadContext;
import com.bbva.elara.utility.api.connector.APIConnector;
import com.bbva.pisd.dto.insurance.amazon.SignatureAWS;
import com.bbva.pisd.dto.insurance.bo.detail.InsuranceQuotationDetailBO;
import com.bbva.pisd.dto.insurance.bo.financing.FinancingPlanBO;
import com.bbva.pisd.dto.insurance.mock.MockDTO;
import com.bbva.pisd.lib.r014.PISDR014;
import com.bbva.pisd.lib.r020.factory.ApiConnectorFactoryTest;
import com.bbva.pisd.lib.r020.impl.PISDR020Impl;
import com.bbva.pisd.mock.MockBundleContext;
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
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestClientException;

import java.io.IOException;

import static org.junit.Assert.*;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
		"classpath:/META-INF/spring/PISDR020-app.xml",
		"classpath:/META-INF/spring/PISDR020-app-test.xml",
		"classpath:/META-INF/spring/PISDR020-arc.xml",
		"classpath:/META-INF/spring/PISDR020-arc-test.xml" })
public class PISDR020Test {

	private static final Logger LOGGER = LoggerFactory.getLogger(PISDR020Test.class);

	private static final String MESSAGE_EXCEPTION = "CONNECTION ERROR";
	private static final String TRACE_ID = "traceId";

	private PISDR020Impl pisdr020 = new PISDR020Impl();

	private PISDR014 pisdr014;

	private MockDTO mockDTO;
	private APIConnector externalApiConnector;

	@Before
	public void setUp() {
		ThreadContext.set(new Context());

		MockBundleContext mockBundleContext = mock(MockBundleContext.class);

		ApiConnectorFactoryTest apiConnectorFactoryMock = new ApiConnectorFactoryTest();
		externalApiConnector = apiConnectorFactoryMock.getAPIConnector(mockBundleContext, false);
		pisdr020.setExternalApiConnector(externalApiConnector);

		mockDTO = MockDTO.getInstance();

		pisdr014 = mock(PISDR014.class);
		pisdr020.setPisdR014(pisdr014);

		when(pisdr014.executeSignatureConstruction(anyString(), anyString(), anyString(), anyString(), anyString()))
				.thenReturn(new SignatureAWS("", "", "", ""));
	}

	@Test
	public void executeSimulateInsuranceQuotationInstallmentPlanServiceOK() throws IOException {
		LOGGER.info("PISDR020Test - Executing executeSimulateInsuranceQuotationInstallmentPlanServiceOK...");

		FinancingPlanBO responseRimac = mockDTO.getSimulateInsuranceQuotationInstallmentPlanResponse();

		when(externalApiConnector.postForObject(anyString(), anyObject(), any()))
				.thenReturn(responseRimac);

		FinancingPlanBO validation = pisdr020.executeSimulateInsuranceQuotationInstallmentPlan(new FinancingPlanBO(), TRACE_ID);
		assertNotNull(validation);
		assertNotNull(validation.getPayload());
		assertNotNull(validation.getPayload().getFechaInicio());
		assertNotNull(validation.getPayload().getCotizacion());
		assertNotNull(validation.getPayload().getFechaFin());
		assertNotNull(validation.getPayload().getFinanciamiento());
		assertNotNull(validation.getPayload().getFinanciamiento().get(0).getDescripcionPeriodo());
		assertNotNull(validation.getPayload().getFinanciamiento().get(0).getMoneda());
		assertNotNull(validation.getPayload().getFinanciamiento().get(0).getMontoCuota());
		assertNotNull(validation.getPayload().getFinanciamiento().get(0).getFinanciamiento());
		assertNotNull(validation.getPayload().getFinanciamiento().get(0).getNumeroCuotasTotales());

	}

	@Test
	public void executeSimulateInsuranceQuotationInstallmentPlanWithRestClientException() {
		LOGGER.info("PISDR020Test - Executing executeSimulateInsuranceQuotationInstallmentPlanServiceOK...");

		when(externalApiConnector.postForObject(anyString(), anyObject(), any()))
				.thenThrow(new RestClientException(MESSAGE_EXCEPTION));

		FinancingPlanBO validation = pisdr020.executeSimulateInsuranceQuotationInstallmentPlan(new FinancingPlanBO(), TRACE_ID);
		assertNull(validation);
	}

}
