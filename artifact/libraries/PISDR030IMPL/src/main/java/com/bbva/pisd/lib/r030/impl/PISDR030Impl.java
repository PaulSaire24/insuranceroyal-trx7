package com.bbva.pisd.lib.r030.impl;

import com.bbva.apx.exception.business.BusinessException;
import com.bbva.pisd.dto.insurance.aso.quotdetail.QuotDetailDAO;
import com.bbva.pisd.dto.insurance.bo.financing.FinancingPlanBO;
import com.bbva.pisd.dto.insurance.financing.FinancingPlanDTO;
import com.bbva.pisd.dto.insurance.utils.PISDErrors;
import com.bbva.pisd.dto.insurance.utils.PISDProperties;
import com.bbva.pisd.dto.insurance.utils.PISDValidation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Objects;

import static org.springframework.util.CollectionUtils.isEmpty;

/**
 * The  interface PISDR030Impl class...
 */
public class PISDR030Impl extends PISDR030Abstract {

	private static final Logger LOGGER = LoggerFactory.getLogger(PISDR030Impl.class);
	private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

	@Override
	public FinancingPlanDTO executeSimulateInsuranceQuotationInstallmentPlan (FinancingPlanDTO input) {
		LOGGER.info("***** PISDR030Impl - executeSimulateInsuranceQuotationInstallmentPlan START *****");
		LOGGER.info("***** PISDR030Impl - executeSimulateInsuranceQuotationInstallmentPlan Param: {} *****", input.getQuotationId());

		FinancingPlanDTO response = new FinancingPlanDTO();

		try {

			Map<String, Object> responseQueryGetQuotationService = this.pisdR012.executeRegisterAdditionalCompanyQuotaId(input.getQuotationId());

			LOGGER.info("***** PISDR030Impl - executeSimulateInsuranceQuotationInstallmentPlan | validateGetInsuranceQuotation *****");

			QuotDetailDAO quotationDetails = validateGetInsuranceQuotation(responseQueryGetQuotationService);

			LOGGER.info("***** PISDR030Impl - executeSimulateInsuranceQuotationInstallmentPlan | isStartDateValid *****");

			isStartDateValid(input);

			FinancingPlanBO requestRimac = this.mapperHelper.createRequestRimac(input, quotationDetails);

			FinancingPlanBO responseRimac = this.pisdR020.executeFinancingPlan(requestRimac, input.getTraceId());
			LOGGER.info("***** PISDR030Impl - validate SimulateInsuranceQuotationInstallmentPlan Service response *****");
			try {
				validateSimulateInsuranceQuotationInstallmentPlanResponse(responseRimac);
			} catch (BusinessException ex) {
				LOGGER.info("***** PISDR030Impl - validate validateSimulateInsuranceQuotationInstallmentPlanResponse -> Response NULL *****");
				return null;
			}

			this.mapperHelper.mapSimulateInsuranceQuotationInstallmentPlanResponseValues(response, responseRimac);
			LOGGER.info("***** PISDR030Impl - mapSimulateInsuranceQuotationInstallmentPlanResponseValues Response with Rimac values: {} *****", response);

		}

		catch (BusinessException ex) {
			LOGGER.info("***** PISDR030Impl - executeGetInsuranceQuotation | Business exception message: {} *****", ex.getMessage());
			return null;
		}

		return response;
	}

	private void isStartDateValid(FinancingPlanDTO input){
		try {
			String now = dateFormat.format(new Date());
			if (Objects.isNull(input.getStartDate()) || input.getStartDate().before(dateFormat.parse(now))) {
				this.addAdvice(PISDErrors.ERROR_SCHEDULE_QUOTE_STARTDATE.getAdviceCode());
				throw PISDValidation.build(PISDErrors.ERROR_SCHEDULE_QUOTE_STARTDATE);
			}
		} catch (ParseException ex){
			LOGGER.info("***** PISDR030Impl - isStartDateValid | Parsing error: {} *****", ex.getMessage());
		}
	}
	private void validateSimulateInsuranceQuotationInstallmentPlanResponse(FinancingPlanBO responseRimac) {
		if(Objects.isNull(responseRimac)) {
			throw PISDValidation.build(PISDErrors.ERROR_CONNECTION_SCHEDULE_QUOTE_RIMAC_SERVICE);
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
