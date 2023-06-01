package com.bbva.pisd.lib.r020;

import com.bbva.elara.domain.transaction.Context;
import com.bbva.elara.domain.transaction.ThreadContext;
import com.bbva.elara.utility.api.connector.APIConnector;
import com.bbva.pisd.dto.insurance.amazon.SignatureAWS;
import com.bbva.pisd.dto.insurance.bo.financing.CronogramaPagoBO;
import com.bbva.pisd.dto.insurance.bo.financing.CronogramaPagoLifeBO;
import com.bbva.pisd.dto.insurance.bo.financing.FinancingPlanBO;
import com.bbva.pisd.dto.insurance.mock.MockDTO;
import com.bbva.pisd.lib.r014.PISDR014;
import com.bbva.pisd.lib.r020.factory.ApiConnectorFactoryTest;
import com.bbva.pisd.lib.r020.impl.PISDR020Impl;
import com.bbva.pisd.lib.r020.impl.util.RimacUrlForker;
import com.bbva.pisd.mock.MockBundleContext;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.junit.Assert.*;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

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
	private RimacUrlForker rimacUrlForker;

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

		rimacUrlForker=mock(RimacUrlForker.class);
		pisdr020.setRimacUrlForker(rimacUrlForker);

		when(pisdr014.executeSignatureConstruction(anyString(), anyString(), anyString(), anyString(), anyString()))
				.thenReturn(new SignatureAWS("", "", "", ""));
	}

	@Test
	public void executeQuoteScheduleServiceOK() throws IOException {
		LOGGER.info("PISDR020Test - Executing executeQuoteScheduleServiceOK...");

		FinancingPlanBO responseRimac = mockDTO.getSimulateInsuranceQuotationInstallmentPlanResponseRimac();

		when(externalApiConnector.postForObject(anyString(), anyObject(), any()))
				.thenReturn(responseRimac);

		when(this.rimacUrlForker.generateUriForSignatureAWSQuoteSchedule(anyString(), anyObject())).thenReturn("/vehicular/V1/cotizacion/cronograma/calcular-cuota");
		when(this.rimacUrlForker.generatePropertyKeyNameQuoteSchedule(anyString())).thenReturn("financingplan.rimac.830");

		FinancingPlanBO validation = pisdr020.executeQuoteSchedule(new FinancingPlanBO(), TRACE_ID, "830", null);
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

		when(externalApiConnector.postForObject(anyString(), anyObject(), any(), anyMap()))
				.thenReturn(responseRimac);

		FinancingPlanBO validation2 = pisdr020.executeQuoteSchedule(new FinancingPlanBO(), TRACE_ID, "832", null);

		assertNotNull(validation2);
		assertNotNull(validation2.getPayload());
		assertNotNull(validation2.getPayload().getFechaInicio());
		assertNotNull(validation2.getPayload().getCotizacion());
		assertNotNull(validation2.getPayload().getFechaFin());
		assertNotNull(validation2.getPayload().getFinanciamiento());
		assertNotNull(validation2.getPayload().getFinanciamiento().get(0).getDescripcionPeriodo());
		assertNotNull(validation2.getPayload().getFinanciamiento().get(0).getMoneda());
		assertNotNull(validation2.getPayload().getFinanciamiento().get(0).getMontoCuota());
		assertNotNull(validation2.getPayload().getFinanciamiento().get(0).getFinanciamiento());
		assertNotNull(validation2.getPayload().getFinanciamiento().get(0).getNumeroCuotasTotales());

	}

	@Test
	public void executePaymentScheduleServiceOK() throws IOException {
		LOGGER.info("PISDR020Test - Executing executePaymentScheduleServiceOK...");

		CronogramaPagoBO responseRimac = mockDTO.getSimulateInsuranceQuotationInstallmentPlanCronogramaPagoResponseRimac();
		when(this.rimacUrlForker.generateUriForSignatureAWSPaymentSchedule(anyString(),anyString())).thenReturn("/vehicular/V1/cotizacion/9a64a5ed-509f-4baa-88e3-a0e373b49e65/cronogramapago");
		when(this.rimacUrlForker.generatePropertyKeyNamePaymentSchedule(anyString())).thenReturn("paymentschedule.rimac.830");
		when(externalApiConnector.postForObject(anyString(), anyObject(), any(), anyMap()))
				.thenReturn(responseRimac);

		CronogramaPagoBO validation = pisdr020.executePaymentSchedule(new FinancingPlanBO(), "123123", TRACE_ID, "830");
		assertNotNull(validation);
		assertNotNull(validation.getPayload());
		assertNotNull(validation.getPayload().get(0).getFechaInicio());
		assertNotNull(validation.getPayload().get(0).getFechaFinal());
		assertNotNull(validation.getPayload().get(0).getNumeroCuotas());
		assertNotNull(validation.getPayload().get(0).getCuotasFinanciamiento().get(0).getCuota());
		assertNotNull(validation.getPayload().get(0).getCuotasFinanciamiento().get(0).getMonto());
		assertNotNull(validation.getPayload().get(0).getCuotasFinanciamiento().get(0).getFechaVencimiento());
	}

	@Test
	public void executePaymentScheduleLifeEasyYesServiceOK() throws IOException {
		LOGGER.info("PISDR020Test - Executing executePaymentScheduleLifeEasyYesServiceOK...");

		CronogramaPagoLifeBO responseRimac = mockDTO.getSimulateInsuranceQuotationInstallmentPlanCronogramaPagoResponseRimacLifeEasyYes();
		when(this.rimacUrlForker.generateUriForSignatureAWSPaymentSchedule(anyString(),anyString())).thenReturn("/api-vida/V1/cotizaciones/58e46e2e-a49d-4943-a5df-db1913954b3d/cronogramapago-seleccionar");
		when(this.rimacUrlForker.generatePropertyKeyNamePaymentSchedule(anyString())).thenReturn("paymentschedule.rimac.840");
		when(this.externalApiConnector.exchange(anyString(), anyObject(),anyObject(), (Class<CronogramaPagoLifeBO>) any(), anyMap())).thenReturn(new ResponseEntity<>(responseRimac, HttpStatus.OK));

		CronogramaPagoLifeBO validation = pisdr020.executePaymentScheduleLife(new FinancingPlanBO(), "1387232", "840",TRACE_ID);
		assertNotNull(validation);
		assertNotNull(validation.getPayload());
		assertNotNull(validation.getPayload().getCotizacion());
		assertNotNull(validation.getPayload().getPrimaNeta());
		assertNotNull(validation.getPayload().getPrimaBruta());
		assertNotNull(validation.getPayload().getFinanciamiento());
		assertNotNull(validation.getPayload().getNumeroCuotas());
		assertNotNull(validation.getPayload().getCuotasFinanciamiento());
		assertNotNull(validation.getPayload().getCuotasFinanciamiento().get(0).getCuota());
		assertNotNull(validation.getPayload().getCuotasFinanciamiento().get(0).getMontoCuota());
		assertNotNull(validation.getPayload().getCuotasFinanciamiento().get(0).getFechaVencimiento());

	}


	@Test
	public void executeQuoteScheduleWithRestClientException() {
		LOGGER.info("PISDR020Test - Executing executeQuoteScheduleWithRestClientException...");

		when(externalApiConnector.postForObject(anyString(), anyObject(), any()))
				.thenThrow(new RestClientException(MESSAGE_EXCEPTION));
		when(this.rimacUrlForker.generateUriForSignatureAWSQuoteSchedule(anyString(), anyObject())).
		thenReturn("/vehicular/V1/cotizacion/cronograma/calcular-cuota");
		when(this.rimacUrlForker.generatePropertyKeyNameQuoteSchedule(anyString())).
		thenReturn("financingplan.rimac.830");
		FinancingPlanBO validation = pisdr020.executeQuoteSchedule(new FinancingPlanBO(), TRACE_ID, "830", null);
		assertNull(validation);
	}


	@Test
	public void executePaymentScheduleWithRestClientException() {
		LOGGER.info("PISDR020Test - Executing executePaymentScheduleWithRestClientException...");

		when(externalApiConnector.postForObject(anyString(), anyObject(), any(), anyMap()))
				.thenThrow(new RestClientException(MESSAGE_EXCEPTION));
		when(this.rimacUrlForker.generateUriForSignatureAWSPaymentSchedule(anyString(),anyString())).
		thenReturn("/vehicular/V1/cotizacion/9a64a5ed-509f-4baa-88e3-a0e373b49e65/cronogramapago");
		when(this.rimacUrlForker.generatePropertyKeyNamePaymentSchedule(anyString())).
		thenReturn("paymentschedule.rimac.830");
		CronogramaPagoBO validation = pisdr020.executePaymentSchedule(new FinancingPlanBO(), "123123", TRACE_ID, "830");
		assertNull(validation);
	}

	@Test
	public void executePaymentScheduleLifeWithRestClientException() throws IOException{
		LOGGER.info("PISDR020Test - Executing executePaymentScheduleLifeWithRestClientException...");

		when(this.externalApiConnector.exchange(anyString(), anyObject(),anyObject(), (Class<CronogramaPagoLifeBO>) any(), anyMap()))
				.thenThrow(new RestClientException(MESSAGE_EXCEPTION));
		when(this.rimacUrlForker.generateUriForSignatureAWSPaymentSchedule(anyString(),anyString())).
				thenReturn("/api-vida/V1/cotizaciones//api-vida/V1/cotizaciones/{idCotizacion}/cronogramapago-seleccionar/cronogramapago-seleccionar");
		when(this.rimacUrlForker.generatePropertyKeyNamePaymentSchedule(anyString())).
				thenReturn("paymentschedule.rimac.840");

		CronogramaPagoLifeBO validation = pisdr020.executePaymentScheduleLife(new FinancingPlanBO(), "1231423", "830",TRACE_ID);
		assertNull(validation);
	}

}
