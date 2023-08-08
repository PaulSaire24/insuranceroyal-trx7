package com.bbva.pisd.lib.r030.impl.factory;


import com.bbva.pisd.dto.insurance.aso.quotdetail.QuotDetailDAO;
import com.bbva.pisd.dto.insurance.bo.financing.FinancingPlanBO;
import com.bbva.pisd.dto.insurance.financing.FinancingPlanDTO;

public abstract class RequestSchedule {

    public abstract FinancingPlanBO createRequestCalculateQuoteRimac(FinancingPlanDTO input, QuotDetailDAO quotationDetails);

    public abstract FinancingPlanBO createRequestPaymentScheduleRimac(FinancingPlanDTO input);

}
