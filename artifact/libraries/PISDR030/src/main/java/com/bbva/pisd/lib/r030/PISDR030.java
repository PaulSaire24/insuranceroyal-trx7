package com.bbva.pisd.lib.r030;

import com.bbva.pisd.dto.insurance.financing.EntityOutFinancingPlanDTO;
import com.bbva.pisd.dto.insurance.financing.FinancingPlanDTO;

/**
 * The  interface PISDR030 class...
 */
public interface PISDR030 {

	FinancingPlanDTO executeSimulateInsuranceQuotationInstallmentPlan (FinancingPlanDTO input);

}
