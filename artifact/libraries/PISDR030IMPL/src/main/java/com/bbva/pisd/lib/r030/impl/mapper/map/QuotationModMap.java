package com.bbva.pisd.lib.r030.impl.mapper.map;

import com.bbva.pisd.dto.insurance.financing.FinancingPlanDTO;
import com.bbva.pisd.dto.insurance.utils.PISDProperties;
import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.Map;

public class QuotationModMap {

    private QuotationModMap(){}

    public static Map<String, Object> mapInUpdateInsuranceQuotationModAmount(FinancingPlanDTO input, FinancingPlanDTO financingPlanDTO, String productId, String modalityType,String periodId) {
        String frequency = StringUtils.defaultString(periodId);

        Map<String, Object> arguments = new HashMap<>();
        arguments.put(PISDProperties.FIELD_PREMIUM_AMOUNT.getValue(), financingPlanDTO.getInstallmentPlans().get(0).getPaymentAmount().getAmount());
        arguments.put(PISDProperties.FIELD_PREMIUM_CURRENCY_ID.getValue(), financingPlanDTO.getInstallmentPlans().get(0).getPaymentAmount().getCurrency());
        arguments.put(PISDProperties.FIELD_POLICY_PAYMENT_FREQUENCY_TYPE.getValue(), frequency);
        arguments.put(PISDProperties.FIELD_USER_AUDIT_ID.getValue(), input.getUserAudit());
        arguments.put(PISDProperties.FIELD_POLICY_QUOTA_INTERNAL_ID.getValue(), input.getQuotationId());
        arguments.put(PISDProperties.FIELD_OR_FILTER_INSURANCE_PRODUCT_ID.getValue(), productId);
        arguments.put(PISDProperties.FIELD_OR_FILTER_INSURANCE_MODALITY_TYPE.getValue(), modalityType);
        return arguments;
    }

}
