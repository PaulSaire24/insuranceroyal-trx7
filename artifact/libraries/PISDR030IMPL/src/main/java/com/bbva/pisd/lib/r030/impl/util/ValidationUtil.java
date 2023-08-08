package com.bbva.pisd.lib.r030.impl.util;

import com.bbva.pisd.dto.insurance.bo.financing.CronogramaPagoBO;
import com.bbva.pisd.dto.insurance.bo.financing.CronogramaPagoLifeBO;
import com.bbva.pisd.dto.insurance.bo.financing.FinancingPlanBO;
import com.bbva.pisd.dto.insurance.utils.PISDErrors;
import com.bbva.pisd.dto.insurance.utils.PISDValidation;

import java.util.Objects;

public class ValidationUtil {

    private ValidationUtil(){}

    public static void validateSimulateInsuranceQuotationInstallmentPlanResponse(FinancingPlanBO responseRimac) {
        if(Objects.isNull(responseRimac)) {
            throw PISDValidation.build(PISDErrors.ERROR_CONNECTION_SCHEDULE_QUOTE_RIMAC_SERVICE);
        }
    }

    public static void validateResponseExecutePaymentScheduleLife(CronogramaPagoLifeBO responseRimac) {
        if(Objects.isNull(responseRimac)) {
            throw PISDValidation.build(PISDErrors.ERROR_CONNECTION_PAYMENT_SCHEDULE_RIMAC_SERVICE);
        }
    }

    public static void validateSimulateInsuranceQuotationInstallmentPlanResponse(CronogramaPagoBO responseRimac) {
        if(Objects.isNull(responseRimac)) {
            throw PISDValidation.build(PISDErrors.ERROR_CONNECTION_PAYMENT_SCHEDULE_RIMAC_SERVICE);
        }
    }



}
