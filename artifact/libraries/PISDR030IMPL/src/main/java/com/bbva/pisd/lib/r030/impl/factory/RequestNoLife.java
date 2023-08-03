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

public class RequestNoLife extends RequestSchedule {


    public FinancingPlanBO createRequestCalculateQuoteRimac(FinancingPlanDTO input, QuotDetailDAO quotationDetails) {
        FinancingPlanBO requestRimac = new FinancingPlanBO();
        FinanciamientoPayloadBO financiamientoPayloadBO = new FinanciamientoPayloadBO();
        List<FinanciamientoBO> financiamiento = input.getInstallmentPlans().stream().map(this::createCuotaFinanciamiento).collect(Collectors.toList());
        financiamientoPayloadBO.setFinanciamiento(financiamiento);
        financiamientoPayloadBO.setCotizacion(quotationDetails.getRimacId());
        financiamientoPayloadBO.setFechaInicioFinanciamiento(input.getStartDate());
        requestRimac.setPayload(financiamientoPayloadBO);
        return requestRimac;
    }

    @Override
    public FinancingPlanBO createRequestPaymentScheduleRimac(FinancingPlanDTO input) {
        FinancingPlanBO requestRimac = new FinancingPlanBO();
        FinanciamientoPayloadBO financiamientoPayloadBO = new FinanciamientoPayloadBO();
        List<FinanciamientoBO> financiamiento = input.getInstallmentPlans().stream().map(installment -> createCronogramaFinanciamientoLife(installment, input)).collect(Collectors.toList());

        financiamientoPayloadBO.setFinanciamiento(financiamiento);
        requestRimac.setPayload(financiamientoPayloadBO);
        return requestRimac;
    }

    public FinanciamientoBO createCronogramaFinanciamientoLife(InstallmentsDTO installmentsDTO,FinancingPlanDTO input) {
        FinanciamientoBO financiamientoBO = new FinanciamientoBO();
        String frecuencia = PropertiesSingleton.getValue(Constants.RIMAC + installmentsDTO.getPeriod().getId());
        String numeroCuotas =  PropertiesSingleton.getValue(Constants.CUOTA + installmentsDTO.getPeriod().getId());
        financiamientoBO.setFrecuencia(frecuencia);
        financiamientoBO.setNumeroCuotas(Long.parseLong(numeroCuotas));
        financiamientoBO.setFechaInicio(input.getStartDate());
        return financiamientoBO;
    }

    private FinanciamientoBO createCuotaFinanciamiento (InstallmentsDTO installmentsDTO) {
        FinanciamientoBO financiamientoBO = new FinanciamientoBO();
        String periodoId =  PropertiesSingleton.getValue(Constants.RIMAC + installmentsDTO.getPeriod().getId());
        String nroCuotas =  PropertiesSingleton.getValue(Constants.CUOTA + installmentsDTO.getPeriod().getId());
        financiamientoBO.setPeriodo(periodoId);
        financiamientoBO.setNroCuotas(Long.parseLong(nroCuotas));
        return financiamientoBO;
    }

}
