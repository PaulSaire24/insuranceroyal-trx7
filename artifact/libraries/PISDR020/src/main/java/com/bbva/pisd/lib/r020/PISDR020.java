package com.bbva.pisd.lib.r020;

import com.bbva.pisd.dto.insurance.bo.financing.FinancingPlanBO;

/**
 * The  interface PISDR020 class...
 */
public interface PISDR020 {

	FinancingPlanBO executeFinancingPlan (FinancingPlanBO request, String traceId);


}
