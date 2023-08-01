package com.bbva.pisd.lib.r030.impl;

import com.bbva.apx.exception.business.BusinessException;
import com.bbva.pisd.dto.insurance.aso.quotdetail.QuotDetailDAO;
import com.bbva.pisd.dto.insurance.bo.financing.CronogramaPagoBO;
import com.bbva.pisd.dto.insurance.bo.financing.CronogramaPagoLifeBO;
import com.bbva.pisd.dto.insurance.bo.financing.FinancingPlanBO;
import com.bbva.pisd.dto.insurance.commons.InstallmentsDTO;
import com.bbva.pisd.dto.insurance.financing.FinancingPlanDTO;
import com.bbva.pisd.dto.insurance.utils.PISDConstants;
import com.bbva.pisd.dto.insurance.utils.PISDErrors;
import com.bbva.pisd.dto.insurance.utils.PISDProperties;
import com.bbva.pisd.dto.insurance.utils.PISDValidation;
import com.bbva.pisd.lib.r030.impl.mapper.map.QuotationModMap;
import com.bbva.pisd.lib.r030.impl.util.ValidationUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.springframework.util.CollectionUtils.isEmpty;

/**
 * The  interface PISDR030Impl class...
 */
public class PISDR030Impl extends PISDR030Abstract {

	private static final Logger LOGGER = LoggerFactory.getLogger(PISDR030Impl.class);
	private static final String ARRAY_PERIOD = "ARRAY_PERIOD";
	private static final String BUSINESS_NAME_VIDA = "VIDA";
	private static final String FIELD_PRODUCT_SHORT_DESC = "PRODUCT_SHORT_DESC";

	@Override
	public FinancingPlanDTO executeSimulateInsuranceQuotationInstallmentPlan (FinancingPlanDTO input) {
		LOGGER.info("***** PISDR030Impl - executeSimulateInsuranceQuotationInstallmentPlan START *****");
		LOGGER.info("***** PISDR030Impl - executeSimulateInsuranceQuotationInstallmentPlan Param: {} *****", input);

		FinancingPlanDTO response = new FinancingPlanDTO();

		try {

			LOGGER.info("***** PISDR030Impl - executeSimulateInsuranceQuotationInstallmentPlan | validatePeriod *****");

			validatePeriod(input);

			Map<String, Object> responseQueryGetQuotationService = this.pisdR012.executeRegisterAdditionalCompanyQuotaId(input.getQuotationId());

			LOGGER.info("***** PISDR030Impl - executeSimulateInsuranceQuotationInstallmentPlan | validateGetInsuranceQuotation *****");

			QuotDetailDAO quotationDetails = validateGetInsuranceQuotation(responseQueryGetQuotationService);

			LOGGER.info("***** PISDR030Impl - executeSimulateInsuranceQuotationInstallmentPlan | isStartDateValid *****");
			String productType = (String) responseQueryGetQuotationService.get(PISDProperties.FILTER_INSURANCE_PRODUCT_TYPE.getValue());
			String modalityType = (String) responseQueryGetQuotationService.get(PISDProperties.FIELD_OR_FILTER_INSURANCE_MODALITY_TYPE.getValue());
			String insuranceBusinessName = (String) responseQueryGetQuotationService.get(PISDProperties.FIELD_INSURANCE_BUSINESS_NAME.getValue());
			String productShortDesc = (String) responseQueryGetQuotationService.get(FIELD_PRODUCT_SHORT_DESC);
			response = isStartDateValid(input, quotationDetails, productType, modalityType,insuranceBusinessName,productShortDesc);
		}

		catch (BusinessException ex) {
			LOGGER.info("***** PISDR030Impl - executeRegisterAdditionalCompanyQuotaId | Business exception message: {} *****", ex.getMessage());
			return null;
		}

		return response;
	}

	private FinancingPlanDTO executeQuoteSchedule (FinancingPlanDTO input, QuotDetailDAO quotationDetails, String productId,String insuranceBusinessName,String productShortDesc) {

		LOGGER.info("***** PISDR030Impl - executeQuoteSchedule *****");

		FinancingPlanBO requestRimac = new FinancingPlanBO();
		if(Objects.nonNull(insuranceBusinessName) && insuranceBusinessName.equals(BUSINESS_NAME_VIDA)) {
			requestRimac = this.mapperHelper.createRequestQuoteScheduleRimacLife(input,productShortDesc);
		} else {
			requestRimac = this.mapperHelper.createRequestQuoteScheduleRimac(input, quotationDetails);
		}

		FinancingPlanBO responseRimac = this.pisdR020.executeQuoteSchedule(requestRimac, input.getTraceId(), productId, quotationDetails.getRimacId());
		LOGGER.info("***** PISDR030Impl - validate SimulateInsuranceQuotationInstallmentPlan Service response *****");
		try {
			ValidationUtil.validateSimulateInsuranceQuotationInstallmentPlanResponse(responseRimac);
		} catch (BusinessException ex) {
			LOGGER.info("***** PISDR030Impl - validate validateSimulateInsuranceQuotationInstallmentPlanResponse -> Response NULL *****");
			return null;
		}
		return this.mapperHelper.mapSimulateInsuranceQuotationInstallmentPlanResponseValues(responseRimac);
	}

	private FinancingPlanDTO executePaymentSchedule (FinancingPlanDTO input, QuotDetailDAO quotationDetails, String productId,String insuranceBusinessName,String productShortDesc) {

		LOGGER.info("***** PISDR030Impl - executePaymentSchedule *****");

		if(Objects.nonNull(insuranceBusinessName) && insuranceBusinessName.equals(BUSINESS_NAME_VIDA)) {
			return getFinancingPlanLife(input, quotationDetails, productId,productShortDesc);
		}

		FinancingPlanBO requestRimac = this.mapperHelper.createRequestPaymentScheduleRimac(input);
		CronogramaPagoBO responseRimac = this.pisdR020.executePaymentSchedule(requestRimac, quotationDetails.getRimacId(), input.getTraceId(), productId);
		LOGGER.info("***** PISDR030Impl - validate SimulateInsuranceQuotationInstallmentPlan Service response *****");
		try {
			ValidationUtil.validateSimulateInsuranceQuotationInstallmentPlanResponse(responseRimac);
		} catch (BusinessException ex) {
			LOGGER.info("***** PISDR030Impl - validate validateSimulateInsuranceQuotationInstallmentPlanResponse -> Response NULL *****");
			return null;
		}
		return this.mapperHelper.mapSimulateInsuranceQuotationInstallmentPlanResponseValues(input, responseRimac);
	}

	private FinancingPlanDTO getFinancingPlanLife(FinancingPlanDTO input, QuotDetailDAO quotationDetails, String productId,String productShortDesc) {
		FinancingPlanBO requestRimac = this.mapperHelper.createRequestPaymentScheduleRimacLifeEasyYes(input,productShortDesc);
		LOGGER.info("***** PISDR030Impl - getFinancingPlanLifeEasyYes | requestRimac {} *****",requestRimac);

		CronogramaPagoLifeBO responseRimac = this.pisdR020.executePaymentScheduleLife(requestRimac, quotationDetails.getRimacId(), productId, input.getTraceId());
		LOGGER.info("***** PISDR030Impl - getFinancingPlanLifeEasyYes | responseRimac {} *****",responseRimac);

		try{
			ValidationUtil.validateResponseExecutePaymentScheduleLife(responseRimac);
		}catch (BusinessException ex) {
			LOGGER.info("***** PISDR030Impl - validate validateResponseExecutePaymentScheduleLife -> Response NULL - exception {} *****",ex.getMessage());
			return null;
		}

		return this.mapperHelper.mapSimulatePaymentScheduleLifeEasyYesResponse(input, responseRimac);
	}

	public FinancingPlanDTO isStartDateValid(FinancingPlanDTO input, QuotDetailDAO quotationDetails, String productId, String modalityType,String insuranceBusinessName,String productShortDesc) {
		FinancingPlanDTO financingPlanDTO = new FinancingPlanDTO();
		LocalDate date = new LocalDate();
		if (Objects.isNull(input.getStartDate())) {
			input.setStartDate(date);
			financingPlanDTO = executeQuoteSchedule(input,quotationDetails, productId,insuranceBusinessName,productShortDesc);
		} else if(Objects.nonNull(input.getStartDate()) && input.getStartDate().isAfter(date.minusDays(1))) {
			financingPlanDTO = executePaymentSchedule(input,quotationDetails, productId,insuranceBusinessName,productShortDesc);
		} else {
			financingPlanDTO = null;
			this.addAdvice(PISDErrors.ERROR_SCHEDULE_QUOTE_STARTDATE.getAdviceCode());
			throw PISDValidation.build(PISDErrors.ERROR_SCHEDULE_QUOTE_STARTDATE);
		}
		if (financingPlanDTO != null) {
			LOGGER.info("***** PISDR030Impl - isStartDateValid - financingPlanDTO: {}", financingPlanDTO);
			LOGGER.info("***** PISDR030Impl - isStartDateValid - property: {}", "update.quotation.amount.".concat(StringUtils.defaultString(productId)).concat(StringUtils.defaultString(input.getSaleChannelId()).toLowerCase()));
			boolean isUpdateQuotationAmount = BooleanUtils.toBoolean(this.applicationConfigurationService.getProperty("update.quotation.amount.".concat(StringUtils.defaultString(productId)).concat(".").concat(StringUtils.defaultString(input.getSaleChannelId()).toLowerCase())));
			LOGGER.info("***** PISDR030Impl - isStartDateValid - isUpdateQuotationAmount: {}", isUpdateQuotationAmount);
			if (isUpdateQuotationAmount && input.getInstallmentPlans().size() == 1) {
				String periodId = this.applicationConfigurationService.getProperty(financingPlanDTO.getInstallmentPlans().get(0).getPeriod().getId());
				Map<String, Object> argumentsForUpdateInsuranceQuotationModAmount = QuotationModMap.mapInUpdateInsuranceQuotationModAmount(input, financingPlanDTO, productId, modalityType,periodId);
				LOGGER.info("***** PISDR030Impl - isStartDateValid - argumentsForUpdateInsuranceQuotationModAmount: {}", argumentsForUpdateInsuranceQuotationModAmount);
				this.pisdR012.executeUpdateInsuranceQuotationModAmount(argumentsForUpdateInsuranceQuotationModAmount);
			}
		}
		return financingPlanDTO;
	}


	private QuotDetailDAO validateGetInsuranceQuotation(Map<String, Object> responseQueryGetQuotationService) {
		if(isEmpty(responseQueryGetQuotationService)) {
			throw PISDValidation.build(PISDErrors.NON_EXISTENT_QUOTATION);
		}
		QuotDetailDAO quotationDetails = new QuotDetailDAO();
		quotationDetails.setRimacId((String) responseQueryGetQuotationService.get(PISDProperties.FIELD_INSURANCE_COMPANY_QUOTA_ID.getValue()));
		return quotationDetails;
	}

	private void validatePeriod(FinancingPlanDTO input){
		List arrayPeriod =  Arrays.asList(this.applicationConfigurationService.getProperty(ARRAY_PERIOD).split(","));
		List<InstallmentsDTO> period = input.getInstallmentPlans().stream()
				.filter(p -> arrayPeriod.contains(p.getPeriod().getId())).collect(Collectors.toList());
		if(input.getInstallmentPlans().size() > period.size()) {
			this.addAdvice(PISDErrors.ERROR_NOT_PERIOD_VALIDATE.getAdviceCode());
			throw PISDValidation.build(PISDErrors.ERROR_NOT_PERIOD_VALIDATE);
		}
	}


}
