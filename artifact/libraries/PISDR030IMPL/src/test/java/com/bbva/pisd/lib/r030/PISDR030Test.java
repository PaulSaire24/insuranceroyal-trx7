package com.bbva.pisd.lib.r030;

import com.bbva.elara.configuration.manager.application.ApplicationConfigurationService;
import com.bbva.elara.domain.transaction.Context;
import com.bbva.elara.domain.transaction.ThreadContext;
import com.bbva.pisd.dto.insurance.bo.financing.*;
import com.bbva.pisd.dto.insurance.commons.InstallmentsDTO;
import com.bbva.pisd.dto.insurance.commons.PaymentPeriodDTO;
import com.bbva.pisd.dto.insurance.financing.FinancingPlanDTO;
import com.bbva.pisd.dto.insurance.mock.MockDTO;
import com.bbva.pisd.dto.insurance.policy.PaymentAmountDTO;
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
import java.math.BigDecimal;
import java.util.*;

import static org.junit.Assert.*;
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

	private Map<String, Object> responseQueryGetQuotationService;

	private FinancingPlanBO requestRimac;
	private FinancingPlanBO responseRimac;

	@Before
	public void setUp() throws IOException {
		ThreadContext.set(new Context());

		mockDTO = MockDTO.getInstance();

		applicationConfigurationService = mock(ApplicationConfigurationService.class);

		when(applicationConfigurationService.getProperty("ARRAY_PERIOD")).thenReturn("ANNUAL,BIMONTHLY,MONTHLY,SEMIANNUAL,QUARTERLY");
		when(applicationConfigurationService.getProperty("update.quotation.amount..")).thenReturn("false");
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
		when(responseQueryGetQuotationService.get(PISDProperties.FIELD_INSURANCE_BUSINESS_NAME.getValue())).thenReturn("VEHICULAR");
		when(responseQueryGetQuotationService.get("PRODUCT_SHORT_DESC")).thenReturn("VEHICULAR");

		when(this.applicationConfigurationService.getProperty("CUOTAMONTHLY")).thenReturn("12");
		when(this.applicationConfigurationService.getProperty("CUOTAANNUAL")).thenReturn("1");
		when(this.applicationConfigurationService.getProperty("CUOTASEMIANNUAL")).thenReturn("2");
		when(this.applicationConfigurationService.getProperty("RIMACMONTHLY")).thenReturn("M");
		when(this.applicationConfigurationService.getProperty("RIMACANNUAL")).thenReturn("A");
		when(this.applicationConfigurationService.getProperty("RIMACSEMIANNUAL")).thenReturn("R");

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
	public void executeQuoteScheduleWithResponseVida() throws IOException {
		LOGGER.info("PISDR030Test Executing executeQuoteScheduleWithResponse ...");
		Map<String, Object> responseQueryGetQuotationService1;
		responseQueryGetQuotationService1 = mock(Map.class);

		when(responseQueryGetQuotationService1.get(PISDProperties.FIELD_INSURANCE_COMPANY_QUOTA_ID.getValue())).thenReturn("9a64a5ed-509f-4baa-88e3-a0e373b49e65");
		when(responseQueryGetQuotationService1.get(PISDProperties.FILTER_INSURANCE_PRODUCT_TYPE.getValue())).thenReturn("840");
		when(responseQueryGetQuotationService1.get(PISDProperties.FIELD_INSURANCE_BUSINESS_NAME.getValue())).thenReturn("VIDA");
		when(responseQueryGetQuotationService1.get("PRODUCT_SHORT_DESC")).thenReturn("EASYYES");

		when(pisdr012.executeRegisterAdditionalCompanyQuotaId(anyString())).thenReturn(responseQueryGetQuotationService1);

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

	@Test
	public void executeUpdateQuotationAmountOK() {
		LOGGER.info("PISDR030Test Executing executeUpdateQuotationAmountOK ...");
		Map<String, Object> responseExecuteRegisterAdditionalCompanyQuotaId = new HashMap<>();
		responseExecuteRegisterAdditionalCompanyQuotaId.put(PISDProperties.FILTER_INSURANCE_PRODUCT_TYPE.getValue(), "833");
		responseExecuteRegisterAdditionalCompanyQuotaId.put(PISDProperties.FIELD_OR_FILTER_INSURANCE_MODALITY_TYPE.getValue(), "07");
		responseExecuteRegisterAdditionalCompanyQuotaId.put(PISDProperties.FIELD_INSURANCE_COMPANY_QUOTA_ID.getValue(), "12345678");
		responseExecuteRegisterAdditionalCompanyQuotaId.put(PISDProperties.FIELD_INSURANCE_BUSINESS_NAME.getValue(),"VEHICULAR");
		responseExecuteRegisterAdditionalCompanyQuotaId.put("PRODUCT_SHORT_DESC","VEHICULAR");

		FinancingPlanBO response = new FinancingPlanBO();
		response.setPayload(new FinanciamientoPayloadBO());
		response.getPayload().setFechaInicio(new LocalDate());
		response.getPayload().setFechaFin(new LocalDate());
		response.getPayload().setFinanciamiento(new ArrayList<>());
		response.getPayload().getFinanciamiento().add(new FinanciamientoBO());
		response.getPayload().getFinanciamiento().get(0).setMontoCuota(BigDecimal.TEN);
		response.getPayload().getFinanciamiento().get(0).setMoneda("USD");
		response.getPayload().getFinanciamiento().get(0).setDescripcionPeriodo("");
		response.getPayload().getFinanciamiento().get(0).setNumeroCuotasTotales(12L);

		FinancingPlanDTO financingPlanDTO = new FinancingPlanDTO();
		financingPlanDTO.setQuotationId("12345678");
		financingPlanDTO.setInstallmentPlans(new ArrayList<>());
		financingPlanDTO.getInstallmentPlans().add(new InstallmentsDTO());
		financingPlanDTO.getInstallmentPlans().get(0).setPeriod(new PaymentPeriodDTO());
		financingPlanDTO.getInstallmentPlans().get(0).getPeriod().setId("MONTHLY");
		financingPlanDTO.getInstallmentPlans().get(0).setPaymentAmount(new PaymentAmountDTO());
		financingPlanDTO.getInstallmentPlans().get(0).getPaymentAmount().setAmount(10.00);
		financingPlanDTO.getInstallmentPlans().get(0).getPaymentAmount().setCurrency("USD");

		when(pisdr012.executeRegisterAdditionalCompanyQuotaId(anyString())).thenReturn(responseExecuteRegisterAdditionalCompanyQuotaId);
		when(pisdr020.executeQuoteSchedule(anyObject(), anyString(), anyString(), anyString())).thenReturn(response);
		when(mapperHelper.mapSimulateInsuranceQuotationInstallmentPlanResponseValues(anyObject())).thenReturn(financingPlanDTO);
		when(applicationConfigurationService.getProperty("update.quotation.amount.833.")).thenReturn("true");
		when(applicationConfigurationService.getProperty("MONTHLY")).thenReturn("M");
		when(pisdr012.executeUpdateInsuranceQuotationModAmount(anyMap())).thenReturn(1);

		FinancingPlanDTO validation = pisdr030.executeSimulateInsuranceQuotationInstallmentPlan(financingPlanDTO);
		assertNotNull(validation);
	}

	private FinancingPlanDTO requestTrxMonthlyFrquency(){
		FinancingPlanDTO financingPlanDTO = new FinancingPlanDTO();

		financingPlanDTO.setQuotationId("0814000042574");
		financingPlanDTO.setStartDate(new LocalDate());
		List<InstallmentsDTO> installmentsDTOList = new ArrayList<>();
		InstallmentsDTO installmentsDTO = new InstallmentsDTO();
		PaymentPeriodDTO paymentPeriodDTO = new PaymentPeriodDTO();
		paymentPeriodDTO.setId("MONTHLY");
		installmentsDTO.setPeriod(paymentPeriodDTO);
		installmentsDTOList.add(installmentsDTO);
		financingPlanDTO.setInstallmentPlans(installmentsDTOList);

		return financingPlanDTO;
	}

	@Test
	public void executePaymentScheduleLifeEasyYesOK() throws IOException {
		LOGGER.info("PISDR030Test Executing executePaymentScheduleLifeEasyYes ...");

		FinancingPlanDTO requestTrxMonthlyFrquency = this.requestTrxMonthlyFrquency();
		CronogramaPagoLifeBO responseRimac = mockDTO.getSimulateInsuranceQuotationInstallmentPlanCronogramaPagoResponseRimacLifeEasyYes();

		Map<String, Object> responseQueryGetQuotationService = new HashMap<>();
		responseQueryGetQuotationService.put(PISDProperties.FILTER_INSURANCE_PRODUCT_TYPE.getValue(), "840");
		responseQueryGetQuotationService.put(PISDProperties.FIELD_OR_FILTER_INSURANCE_MODALITY_TYPE.getValue(), "01");
		responseQueryGetQuotationService.put(PISDProperties.FIELD_INSURANCE_COMPANY_QUOTA_ID.getValue(), "1f142c09-640d-4173-8a3d-6d2b24mf4e93");
		responseQueryGetQuotationService.put(PISDProperties.FIELD_INSURANCE_BUSINESS_NAME.getValue(),"VIDA");
		responseQueryGetQuotationService.put("PRODUCT_SHORT_DESC","EASYYES");

		FinancingPlanDTO responseSchedule = new FinancingPlanDTO();
		responseSchedule.setStartDate(new LocalDate());
		responseSchedule.setMaturityDate(new LocalDate());
		responseSchedule.setInstallmentPlans(new ArrayList<>());
		responseSchedule.getInstallmentPlans().add(new InstallmentsDTO());
		responseSchedule.getInstallmentPlans().get(0).setPeriod(new PaymentPeriodDTO());
		responseSchedule.getInstallmentPlans().get(0).getPeriod().setId("MONTHLY");
		responseSchedule.getInstallmentPlans().get(0).setPaymentAmount(new PaymentAmountDTO());
		responseSchedule.getInstallmentPlans().get(0).getPaymentAmount().setAmount(15.00);
		responseSchedule.getInstallmentPlans().get(0).getPaymentAmount().setCurrency("PEN");

		when(pisdr012.executeRegisterAdditionalCompanyQuotaId(anyString())).thenReturn(responseQueryGetQuotationService);
		when(pisdr020.executePaymentScheduleLife(anyObject(), anyObject(), anyString(), anyString())).thenReturn(responseRimac);
		when(mapperHelper.mapSimulatePaymentScheduleLifeEasyYesResponse(anyObject(),anyObject())).thenReturn(responseSchedule);
		when(pisdr012.executeUpdateInsuranceQuotationModAmount(anyMap())).thenReturn(1);
		when(applicationConfigurationService.getProperty("update.quotation.amount.840.")).thenReturn("true");

		FinancingPlanDTO validation = pisdr030.executeSimulateInsuranceQuotationInstallmentPlan(requestTrxMonthlyFrquency);

		assertNotNull(validation);
		assertNotNull(validation.getStartDate());
		assertNotNull(validation.getMaturityDate());
		assertNotNull(validation.getInstallmentPlans());
	}

	@Test
	public void executePaymentScheduleLifeEasyYesWithPaymenScheduleLifeNull() throws IOException {
		LOGGER.info("PISDR030Test Executing executePaymentScheduleLifeEasyYesWithPaymenScheduleLifeNull ...");

		Map<String, Object> responseQueryGetQuotationService = new HashMap<>();
		responseQueryGetQuotationService.put(PISDProperties.FILTER_INSURANCE_PRODUCT_TYPE.getValue(), "840");
		responseQueryGetQuotationService.put(PISDProperties.FIELD_OR_FILTER_INSURANCE_MODALITY_TYPE.getValue(), "01");
		responseQueryGetQuotationService.put(PISDProperties.FIELD_INSURANCE_COMPANY_QUOTA_ID.getValue(), "1f142c09-640d-4173-8a3d-6d2b24mf4e93");
		responseQueryGetQuotationService.put(PISDProperties.FIELD_INSURANCE_BUSINESS_NAME.getValue(), "VIDA");
		responseQueryGetQuotationService.put("PRODUCT_SHORT_DESC", "VIDADINAMICO");

		when(pisdr012.executeRegisterAdditionalCompanyQuotaId(anyString())).thenReturn(responseQueryGetQuotationService);
		when(pisdr020.executePaymentScheduleLife(anyObject(), anyObject(), anyString(), anyString())).thenReturn(null);

		FinancingPlanDTO financingPlanDTO = mockDTO.getSimulateInsuranceQuotationInstallmentPlanRequest();
		financingPlanDTO.setStartDate(new LocalDate());
		FinancingPlanDTO validation = pisdr030.executeSimulateInsuranceQuotationInstallmentPlan(financingPlanDTO);
		assertNull(validation);
	}

	private static FinancingPlanDTO generateInputRequest(){
		FinancingPlanDTO request = new FinancingPlanDTO();

		request.setQuotationId("quotationid");
		InstallmentsDTO monthly = new InstallmentsDTO();
		PaymentPeriodDTO paymentM = new PaymentPeriodDTO();
		paymentM.setId("MONTHLY");
		monthly.setPeriod(paymentM);
		InstallmentsDTO annual = new InstallmentsDTO();
		PaymentPeriodDTO paymentA = new PaymentPeriodDTO();
		paymentA.setId("ANNUAL");
		annual.setPeriod(paymentA);
		List<InstallmentsDTO> list = new ArrayList<>();
		list.add(monthly);
		list.add(annual);
		request.setInstallmentPlans(list);

		return request;
	}

	@Test
	public void testExecuteCalculateQuoteLife_OK() throws IOException{

		when(responseQueryGetQuotationService.get(PISDProperties.FILTER_INSURANCE_PRODUCT_TYPE.getValue())).thenReturn("841");
		when(responseQueryGetQuotationService.get(PISDProperties.FIELD_INSURANCE_BUSINESS_NAME.getValue())).thenReturn("VIDA");
		when(responseQueryGetQuotationService.get("PRODUCT_SHORT_DESC")).thenReturn("VIDADINAMICO");
		when(this.pisdr012.executeRegisterAdditionalCompanyQuotaId(anyString())).thenReturn(responseQueryGetQuotationService);

		FinancingPlanDTO financingPlanDTO = new FinancingPlanDTO();
		financingPlanDTO.setQuotationId("12345678");
		financingPlanDTO.setStartDate(new LocalDate("2023-05-06"));
		financingPlanDTO.setMaturityDate(new LocalDate("2023-05-07"));
		financingPlanDTO.setInstallmentPlans(new ArrayList<>());
		financingPlanDTO.getInstallmentPlans().add(new InstallmentsDTO());
		financingPlanDTO.getInstallmentPlans().get(0).setPeriod(new PaymentPeriodDTO());
		financingPlanDTO.getInstallmentPlans().get(0).getPeriod().setId("MONTHLY");
		financingPlanDTO.getInstallmentPlans().get(0).setPaymentAmount(new PaymentAmountDTO());
		financingPlanDTO.getInstallmentPlans().get(0).getPaymentAmount().setAmount(10.00);
		financingPlanDTO.getInstallmentPlans().get(0).getPaymentAmount().setCurrency("PEN");
		when(mapperHelper.mapSimulateInsuranceQuotationInstallmentPlanResponseValues(anyObject())).thenReturn(financingPlanDTO);

		FinancingPlanBO responseRimac = mockDTO.getSimulateInsuranceQuotationInstallmentPlanResponseRimac();
		when(pisdr020.executeQuoteSchedule(anyObject(), anyString(), anyString(), anyString())).thenReturn(responseRimac);

		FinancingPlanDTO input = generateInputRequest();
		FinancingPlanDTO validation = pisdr030.executeSimulateInsuranceQuotationInstallmentPlan(input);

		assertNotNull(validation);
		assertNotNull(validation.getMaturityDate());
		assertNotNull(validation.getStartDate());
		assertNotNull(validation.getInstallmentPlans());
	}

}
