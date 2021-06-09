package com.bbva.pisd.lib.r030.impl.util;

import com.bbva.elara.configuration.manager.application.ApplicationConfigurationService;

import com.bbva.pisd.dto.insurance.aso.quotdetail.QuotDetailDAO;
import com.bbva.pisd.dto.insurance.bo.financing.CuotaFinanciamientoBO;
import com.bbva.pisd.dto.insurance.bo.financing.FinanciamientoPayloadBO;
import com.bbva.pisd.dto.insurance.bo.financing.FinancingPlanBO;
import com.bbva.pisd.dto.insurance.commons.InstallmentsDTO;
import com.bbva.pisd.dto.insurance.commons.PaymentPeriodDTO;
import com.bbva.pisd.dto.insurance.financing.FinancingPlanDTO;
import com.bbva.pisd.dto.insurance.policy.PaymentAmountDTO;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class MapperHelper {

    private final String RIMAC = "RIMAC";
    private final String CUOTA = "CUOTA";

    protected ApplicationConfigurationService applicationConfigurationService;

    public FinancingPlanBO createRequestRimac (FinancingPlanDTO financingPlanDTO, QuotDetailDAO quotDetailDAO) {
        FinancingPlanBO requestRimac = new FinancingPlanBO();
        FinanciamientoPayloadBO financiamientoPayloadBO = new FinanciamientoPayloadBO();
        List<CuotaFinanciamientoBO> financiamiento = financingPlanDTO.getInstallmentPlans().stream().map(installment -> createCuotaFinanciamiento(installment)).collect(Collectors.toList());
        financiamientoPayloadBO.setFinanciamiento(financiamiento);
        financiamientoPayloadBO.setCotizacion(quotDetailDAO.getRimacId());
        financiamientoPayloadBO.setFechaInicioFinanciamiento(financingPlanDTO.getStartDate());
        requestRimac.setPayload(financiamientoPayloadBO);
        return requestRimac;
    }

    private CuotaFinanciamientoBO createCuotaFinanciamiento (InstallmentsDTO installmentsDTO) {
        CuotaFinanciamientoBO cuotaFinanciamientoBO = new CuotaFinanciamientoBO();
        String periodoId =  this.applicationConfigurationService.getProperty(RIMAC + installmentsDTO.getPeriod().getId());
        String nroCuotas =  this.applicationConfigurationService.getProperty(CUOTA + installmentsDTO.getPeriod().getId());
        cuotaFinanciamientoBO.setPeriodo(periodoId);
        cuotaFinanciamientoBO.setNroCuotas(Long.parseLong(nroCuotas));
        return cuotaFinanciamientoBO;
    }

    public void mapSimulateInsuranceQuotationInstallmentPlanResponseValues(FinancingPlanDTO response, FinancingPlanBO responseRimac) {
        response.setQuotationId(responseRimac.getPayload().getCotizacion());
        response.setStartDate(responseRimac.getPayload().getFechaInicio());
        response.setMaturityDate(responseRimac.getPayload().getFechaFin());
        response.setTotalNumberInstallments(Long.valueOf(responseRimac.getPayload().getFinanciamiento().size()));
        List<InstallmentsDTO> installmentsDTOS = responseRimac.getPayload().getFinanciamiento().stream().map(financiamiento -> createInstallment(financiamiento)).collect(Collectors.toList());
        response.setInstallmentPlans(installmentsDTOS);
    }

    private InstallmentsDTO createInstallment (CuotaFinanciamientoBO cuotaFinanciamientoBO) {
        InstallmentsDTO installmentsDTO = new InstallmentsDTO();
        PaymentPeriodDTO period = new PaymentPeriodDTO();
        PaymentAmountDTO amount = new PaymentAmountDTO();
        amount.setAmount(cuotaFinanciamientoBO.getMontoCuota().doubleValue());
        amount.setCurrency(cuotaFinanciamientoBO.getMoneda());
        String periodicidad = cuotaFinanciamientoBO.getDescripcionPeriodo();
        period.setId(this.applicationConfigurationService.getProperty(periodicidad));
        period.setName(periodicidad);
        installmentsDTO.setPeriod(period);
        installmentsDTO.setPaymentAmount(amount);
        return installmentsDTO;
    }

    public Date getNowDate() {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String now = dateFormat.format(new Date());
            return dateFormat.parse(now);
        }
        catch (ParseException ex){
            return new Date();
        }
    }

    public void setApplicationConfigurationService(ApplicationConfigurationService applicationConfigurationService) {
        this.applicationConfigurationService = applicationConfigurationService;
    }
}
