package com.bbva.pisd.lib.r020;

import com.bbva.pisd.dto.insurance.bo.financing.FinancingPlanBO;

/**
 * The  interface PISDR020 class...
 */
public interface PISDR020 {

	FinancingPlanBO executeSimulateInsuranceQuotationInstallmentPlan (FinancingPlanBO request, String traceId);

}
