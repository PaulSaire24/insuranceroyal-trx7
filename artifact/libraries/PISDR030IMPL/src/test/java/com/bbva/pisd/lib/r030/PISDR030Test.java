package com.bbva.pisd.lib.r030;

import com.bbva.elara.configuration.manager.application.ApplicationConfigurationService;
import com.bbva.elara.domain.transaction.Context;
import com.bbva.elara.domain.transaction.ThreadContext;
import com.bbva.pisd.dto.insurance.bo.financing.CronogramaPagoBO;
import com.bbva.pisd.dto.insurance.bo.financing.FinancingPlanBO;
import com.bbva.pisd.dto.insurance.financing.FinancingPlanDTO;
import com.bbva.pisd.dto.insurance.mock.MockDTO;
import com.bbva.pisd.dto.insurance.utils.PISDProperties;
import com.bbva.pisd.lib.r012.PISDR012;
import com.bbva.pisd.lib.r020.PISDR020;
import com.bbva.pisd.lib.r030.impl.PISDR030Impl;
import com.bbva.pisd.lib.r030.impl.util.MapperHelper;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.util.*;

import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
		"classpath:/META-INF/spring/PISDR030-app.xml",
		"classpath:/META-INF/spring/PISDR030-app-test.xml",
		"classpath:/META-INF/spring/PISDR030-arc.xml",
		"classpath:/META-INF/spring/PISDR030-arc-test.xml" })
public class PISDR030Test {
	private static final Logger LOGGER = LoggerFactory.getLogger(PISDR030Test.class);

	private final PISDR030Impl pisdr030 = new PISDR030Impl();

	private ApplicationConfigurationService applicationConfigurationService;
	private MockDTO mockDTO;
	private MapperHelper mapperHelper;
	private PISDR020 pisdr020;
	private PISDR012 pisdr012;

	private FinancingPlanDTO input;
	private FinancingPlanDTO entityOut;

	private Map<String, Object> responseQueryGetQuotationService;

	private FinancingPlanBO requestRimac;
	private FinancingPlanBO responseRimac;

	@Before
	public void setUp() throws IOException {
		ThreadContext.set(new Context());

		mockDTO = MockDTO.getInstance();

		applicationConfigurationService = mock(ApplicationConfigurationService.class);

		when(applicationConfigurationService.getProperty("ARRAY_PERIOD")).thenReturn("ANNUAL,BIMONTHLY,MONTHLY,SEMIANNUAL,QUARTERLY");

		pisdr030.setApplicationConfigurationService(applicationConfigurationService);

		mapperHelper = mock(MapperHelper.class);
		pisdr030.setMapperHelper(mapperHelper);

		pisdr012 = mock(PISDR012.class);
		pisdr030.setPisdR012(pisdr012);

		pisdr020 = mock(PISDR020.class);
		pisdr030.setPisdR020(pisdr020);

		input = mock(FinancingPlanDTO.class);

		responseQueryGetQuotationService = mock(Map.class);

		when(responseQueryGetQuotationService.get(PISDProperties.FIELD_INSURANCE_COMPANY_QUOTA_ID.getValue())).thenReturn("9a64a5ed-509f-4baa-88e3-a0e373b49e65");
		when(responseQueryGetQuotationService.get(PISDProperties.FILTER_INSURANCE_PRODUCT_TYPE.getValue())).thenReturn("830");

		entityOut = mock(FinancingPlanDTO.class);

	}

	@Test
	public void executeSimulateInsuranceQuotationInstallmentPlanWithEmptyResultFromDB() {
		LOGGER.info("PISDR030Test - Executing executeSimulateInsuranceQuotationInstallmentPlanWithEmptyResultFromDB...");

		when(pisdr012.executeRegisterAdditionalCompanyQuotaId(anyString())).thenReturn(null);

		FinancingPlanDTO validation = pisdr030.executeSimulateInsuranceQuotationInstallmentPlan(input);

		assertNull(validation);
	}

	@Test
	public void executeQuoteSchedule() {
		LOGGER.info("PISDR030Test Executing executeQuoteSchedule ...");

		when(pisdr012.executeRegisterAdditionalCompanyQuotaId(anyString())).thenReturn(responseQueryGetQuotationService);

		when(pisdr020.executeQuoteSchedule(anyObject(), anyString(), anyString(), anyString())).thenReturn(null);

		FinancingPlanDTO validation = pisdr030.executeSimulateInsuranceQuotationInstallmentPlan(input);
		assertNull(validation);
	}

	@Test
	public void executeQuoteScheduleWithResponse() throws IOException {
		LOGGER.info("PISDR030Test Executing executeQuoteScheduleWithResponse ...");

		when(pisdr012.executeRegisterAdditionalCompanyQuotaId(anyString())).thenReturn(responseQueryGetQuotationService);

		FinancingPlanBO response = mockDTO.getSimulateInsuranceQuotationInstallmentPlanResponseRimac();

		when(pisdr020.executeQuoteSchedule(anyObject(), anyString(), anyString(), anyString())).thenReturn(response);

		FinancingPlanDTO validation = pisdr030.executeSimulateInsuranceQuotationInstallmentPlan(input);
		assertNull(validation);
	}

	@Test
	public void executePaymentSchedule() throws IOException {
		LOGGER.info("PISDR030Test Executing executePaymentSchedule ...");

		when(pisdr012.executeRegisterAdditionalCompanyQuotaId(anyString())).thenReturn(responseQueryGetQuotationService);
		when(pisdr020.executePaymentSchedule(anyObject(), anyObject(), anyString(), anyString())).thenReturn(null);

		FinancingPlanDTO financingPlanDTO = mockDTO.getSimulateInsuranceQuotationInstallmentPlanRequest();
		financingPlanDTO.setStartDate(new LocalDate().plusDays(2));
		FinancingPlanDTO validation = pisdr030.executeSimulateInsuranceQuotationInstallmentPlan(financingPlanDTO);
		assertNull(validation);
	}

	@Test
	public void executePaymentScheduleWithResponse() throws IOException {
		LOGGER.info("PISDR030Test Executing executePaymentScheduleWithResponse ...");

		when(pisdr012.executeRegisterAdditionalCompanyQuotaId(anyString())).thenReturn(responseQueryGetQuotationService);
		CronogramaPagoBO response = mockDTO.getSimulateInsuranceQuotationInstallmentPlanCronogramaPagoResponseRimac();
		when(pisdr020.executePaymentSchedule(anyObject(), anyObject(), anyString(), anyString())).thenReturn(response);

		FinancingPlanDTO financingPlanDTO = mockDTO.getSimulateInsuranceQuotationInstallmentPlanRequest();
		financingPlanDTO.setStartDate(new LocalDate().plusDays(2));
		FinancingPlanDTO validation = pisdr030.executeSimulateInsuranceQuotationInstallmentPlan(financingPlanDTO);
		assertNull(validation);
	}

	@Test
	public void executePaymentScheduleDateNotRange() throws IOException {
		LOGGER.info("PISDR030Test Executing executePaymentSchedule ...");

		when(pisdr012.executeRegisterAdditionalCompanyQuotaId(anyString())).thenReturn(responseQueryGetQuotationService);

		when(pisdr020.executePaymentSchedule(anyObject(), anyObject(), anyString(), anyString())).thenReturn(null);
		FinancingPlanDTO financingPlanDTO = mockDTO.getSimulateInsuranceQuotationInstallmentPlanRequest();
		financingPlanDTO.setStartDate(new LocalDate().minusDays(2));
		FinancingPlanDTO validation = pisdr030.executeSimulateInsuranceQuotationInstallmentPlan(financingPlanDTO);
		assertNull(validation);
	}

	@Test
	public void executePaymentScheduleDatePeriodNotValid() throws IOException {
		LOGGER.info("PISDR030Test Executing executePaymentScheduleDatePeriodNotValid ...");

		when(pisdr012.executeRegisterAdditionalCompanyQuotaId(anyString())).thenReturn(responseQueryGetQuotationService);

		when(pisdr020.executePaymentSchedule(anyObject(), anyObject(), anyString(), anyString())).thenReturn(null);
		FinancingPlanDTO financingPlanDTO = mockDTO.getSimulateInsuranceQuotationInstallmentPlanRequest();
		financingPlanDTO.getInstallmentPlans().get(0).getPeriod().setId("");
		FinancingPlanDTO validation = pisdr030.executeSimulateInsuranceQuotationInstallmentPlan(financingPlanDTO);
		assertNull(validation);
	}
}
