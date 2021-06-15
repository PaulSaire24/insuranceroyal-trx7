package com.bbva.pisd.lib.r030.impl;

import com.bbva.apx.exception.business.BusinessException;
import com.bbva.pisd.dto.insurance.aso.quotdetail.QuotDetailDAO;
import com.bbva.pisd.dto.insurance.bo.financing.CronogramaPagoBO;
import com.bbva.pisd.dto.insurance.bo.financing.FinancingPlanBO;
import com.bbva.pisd.dto.insurance.financing.FinancingPlanDTO;
import com.bbva.pisd.dto.insurance.utils.PISDErrors;
import com.bbva.pisd.dto.insurance.utils.PISDProperties;
import com.bbva.pisd.dto.insurance.utils.PISDValidation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.Map;
import java.util.Objects;

import static org.springframework.util.CollectionUtils.isEmpty;

/**
 * The  interface PISDR030Impl class...
 */
public class PISDR030Impl extends PISDR030Abstract {

	private static final Logger LOGGER = LoggerFactory.getLogger(PISDR030Impl.class);

	@Override
	public FinancingPlanDTO executeSimulateInsuranceQuotationInstallmentPlan (FinancingPlanDTO input) {
		LOGGER.info("***** PISDR030Impl - executeSimulateInsuranceQuotationInstallmentPlan START *****");
		LOGGER.info("***** PISDR030Impl - executeSimulateInsuranceQuotationInstallmentPlan Param: {} *****", input);

		FinancingPlanDTO response = new FinancingPlanDTO();

		try {

			Map<String, Object> responseQueryGetQuotationService = this.pisdR012.executeRegisterAdditionalCompanyQuotaId(input.getQuotationId());

			LOGGER.info("***** PISDR030Impl - executeSimulateInsuranceQuotationInstallmentPlan | validateGetInsuranceQuotation *****");

			QuotDetailDAO quotationDetails = validateGetInsuranceQuotation(responseQueryGetQuotationService);

			LOGGER.info("***** PISDR030Impl - executeSimulateInsuranceQuotationInstallmentPlan | isStartDateValid *****");

			response = isStartDateValid(input,quotationDetails);

		}

		catch (BusinessException ex) {
			LOGGER.info("***** PISDR030Impl - executeRegisterAdditionalCompanyQuotaId | Business exception message: {} *****", ex.getMessage());
			return null;
		}

		return response;
	}

	private FinancingPlanDTO executeQuoteSchedule (FinancingPlanDTO input, QuotDetailDAO quotationDetails) {

		LOGGER.info("***** PISDR030Impl - executeQuoteSchedule *****");
		FinancingPlanBO requestRimac = this.mapperHelper.createRequestQuoteScheduleRimac(input, quotationDetails);
		FinancingPlanBO responseRimac = this.pisdR020.executeQuoteSchedule(requestRimac, input.getTraceId());
		LOGGER.info("***** PISDR030Impl - validate SimulateInsuranceQuotationInstallmentPlan Service response *****");
		try {
			validateSimulateInsuranceQuotationInstallmentPlanResponse(responseRimac);
		} catch (BusinessException ex) {
			LOGGER.info("***** PISDR030Impl - validate validateSimulateInsuranceQuotationInstallmentPlanResponse -> Response NULL *****");
			return null;
		}
		return this.mapperHelper.mapSimulateInsuranceQuotationInstallmentPlanResponseValues(responseRimac);
	}

	private FinancingPlanDTO executePaymentSchedule (FinancingPlanDTO input, QuotDetailDAO quotationDetails) {

		LOGGER.info("***** PISDR030Impl - executePaymentSchedule *****");
		FinancingPlanBO requestRimac = this.mapperHelper.createRequestPaymentScheduleRimac(input);
		CronogramaPagoBO responseRimac = this.pisdR020.executePaymentSchedule(requestRimac, quotationDetails, input.getTraceId());
		LOGGER.info("***** PISDR030Impl - validate SimulateInsuranceQuotationInstallmentPlan Service response *****");
		try {
			validateSimulateInsuranceQuotationInstallmentPlanResponse(responseRimac);
		} catch (BusinessException ex) {
			LOGGER.info("***** PISDR030Impl - validate validateSimulateInsuranceQuotationInstallmentPlanResponse -> Response NULL *****");
			return null;
		}
		return this.mapperHelper.mapSimulateInsuranceQuotationInstallmentPlanResponseValues(input, responseRimac);
	}

	private FinancingPlanDTO isStartDateValid(FinancingPlanDTO input, QuotDetailDAO quotationDetails) {
		FinancingPlanDTO financingPlanDTO = new FinancingPlanDTO();
		LocalDate date = LocalDate.now();
		java.sql.Date now = java.sql.Date.valueOf(date);
		if (Objects.isNull(input.getStartDate())) {
			input.setStartDate(now);
			financingPlanDTO = executeQuoteSchedule(input,quotationDetails);
		} else if(Objects.nonNull(input.getStartDate()) && input.getStartDate().after(now)) {
			financingPlanDTO = executePaymentSchedule(input,quotationDetails);
		} else {
			financingPlanDTO = null;
			this.addAdvice(PISDErrors.ERROR_SCHEDULE_QUOTE_STARTDATE.getAdviceCode());
			throw PISDValidation.build(PISDErrors.ERROR_SCHEDULE_QUOTE_STARTDATE);
		}
		return financingPlanDTO;
	}

	private void validateSimulateInsuranceQuotationInstallmentPlanResponse(FinancingPlanBO responseRimac) {
		if(Objects.isNull(responseRimac)) {
			throw PISDValidation.build(PISDErrors.ERROR_CONNECTION_SCHEDULE_QUOTE_RIMAC_SERVICE);
		}
	}

	private void validateSimulateInsuranceQuotationInstallmentPlanResponse(CronogramaPagoBO responseRimac) {
		if(Objects.isNull(responseRimac)) {
			throw PISDValidation.build(PISDErrors.ERROR_CONNECTION_PAYMENT_SCHEDULE_RIMAC_SERVICE);
		}
	}

	private QuotDetailDAO validateGetInsuranceQuotation(Map<String, Object> responseQueryGetQuotationService) {
		if(isEmpty(responseQueryGetQuotationService)) {
			throw PISDValidation.build(PISDErrors.NON_EXISTENT_QUOTATION);
		}
		QuotDetailDAO quotationDetails = new QuotDetailDAO();
		quotationDetails.setRimacId((String) responseQueryGetQuotationService.get(PISDProperties.FIELD_INSURANCE_COMPANY_QUOTA_ID.getValue()));
		return quotationDetails;
	}
}
