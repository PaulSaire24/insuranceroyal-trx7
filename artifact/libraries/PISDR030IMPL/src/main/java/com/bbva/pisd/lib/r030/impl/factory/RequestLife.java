package com.bbva.pisd.lib.r030.impl.factory;

import com.bbva.pisd.dto.insurance.aso.quotdetail.QuotDetailDAO;
import com.bbva.pisd.dto.insurance.bo.financing.FinanciamientoBO;
import com.bbva.pisd.dto.insurance.bo.financing.FinanciamientoPayloadBO;
import com.bbva.pisd.dto.insurance.bo.financing.FinancingPlanBO;
import com.bbva.pisd.dto.insurance.commons.InstallmentsDTO;
import com.bbva.pisd.dto.insurance.financing.FinancingPlanDTO;
import com.bbva.pisd.lib.r030.impl.pattern.PropertiesSingleton;
import com.bbva.pisd.lib.r030.impl.util.Constants;

import java.util.List;
import java.util.stream.Collectors;

public class RequestLife extends RequestSchedule {

    private final String productShortDesc;

    public RequestLife(String productShortDesc) {
        this.productShortDesc = productShortDesc;
    }

    @Override
    public FinancingPlanBO createRequestCalculateQuoteRimac(FinancingPlanDTO input, QuotDetailDAO quotationDetails) {
        FinancingPlanBO requestRimac = new FinancingPlanBO();
        FinanciamientoPayloadBO financiamientoPayloadBO = new FinanciamientoPayloadBO();

        List<FinanciamientoBO> financiamiento = input.getInstallmentPlans().stream().map(installment -> createCuotaFinanciamientoLife(installment)).collect(Collectors.toList());

        financiamientoPayloadBO.setFinanciamiento(financiamiento);
        financiamientoPayloadBO.setProducto(this.productShortDesc);

        requestRimac.setPayload(financiamientoPayloadBO);
        return requestRimac;
    }

    @Override
    public FinancingPlanBO createRequestPaymentScheduleRimac(FinancingPlanDTO input) {
        FinancingPlanBO requestRimac = new FinancingPlanBO();
        FinanciamientoPayloadBO financiamientoPayloadBO = new FinanciamientoPayloadBO();
        List<FinanciamientoBO> financiamiento = input.getInstallmentPlans().stream().map(this::createCronogramaFinanciamientoLife).collect(Collectors.toList());

        financiamientoPayloadBO.setFinanciamiento(financiamiento);
        financiamientoPayloadBO.setProducto(productShortDesc);
        requestRimac.setPayload(financiamientoPayloadBO);
        return requestRimac;
    }


    public FinanciamientoBO createCronogramaFinanciamientoLife(InstallmentsDTO installmentsDTO) {
        FinanciamientoBO financiamientoBO = new FinanciamientoBO();
        String frecuencia =  PropertiesSingleton.getValue(Constants.RIMAC + installmentsDTO.getPeriod().getId());
        String numeroCuotas =  PropertiesSingleton.getValue(Constants.CUOTA + installmentsDTO.getPeriod().getId());
        financiamientoBO.setFrecuencia(frecuencia);
        financiamientoBO.setNumeroCuotas(Long.parseLong(numeroCuotas));
        return financiamientoBO;
    }

    private FinanciamientoBO createCuotaFinanciamientoLife (InstallmentsDTO installmentsDTO) {
        FinanciamientoBO financiamientoBO = new FinanciamientoBO();
        String periodoId =  PropertiesSingleton.getValue(Constants.RIMAC + installmentsDTO.getPeriod().getId());
        String nroCuotas =  PropertiesSingleton.getValue(Constants.CUOTA + installmentsDTO.getPeriod().getId());
        financiamientoBO.setPeriodo(periodoId);
        financiamientoBO.setNumeroCuotas(Long.parseLong(nroCuotas));
        return financiamientoBO;
    }

}
