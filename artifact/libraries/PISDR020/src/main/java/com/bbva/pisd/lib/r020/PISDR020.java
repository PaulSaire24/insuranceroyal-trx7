package com.bbva.pisd.lib.r020;

import com.bbva.pisd.dto.insurance.bo.financing.CronogramaPagoBO;
import com.bbva.pisd.dto.insurance.bo.financing.CronogramaPagoLifeBO;
import com.bbva.pisd.dto.insurance.bo.financing.FinancingPlanBO;

/**
 * The  interface PISDR020 class...
 */
public interface PISDR020 {

	FinancingPlanBO executeQuoteSchedule (FinancingPlanBO input, String traceId, String productId, String quotationId);
	CronogramaPagoBO executePaymentSchedule (FinancingPlanBO request, String quotationId, String traceId, String productId);
	CronogramaPagoLifeBO executePaymentScheduleLife(FinancingPlanBO request, String quotationId, String productId, String traceId);

}
