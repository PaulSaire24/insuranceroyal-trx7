package com.bbva.pisd.lib.r030.impl.util;

import com.bbva.elara.configuration.manager.application.ApplicationConfigurationService;

import com.bbva.pisd.dto.insurance.aso.quotdetail.QuotDetailDAO;
import com.bbva.pisd.dto.insurance.bo.DatoParticularBO;
import com.bbva.pisd.dto.insurance.bo.financing.CuotaFinanciamientoBO;
import com.bbva.pisd.dto.insurance.bo.financing.FinanciamientoPayloadBO;
import com.bbva.pisd.dto.insurance.bo.financing.FinancingPlanBO;
import com.bbva.pisd.dto.insurance.bo.simulation.InsuranceSimulationBO;
import com.bbva.pisd.dto.insurance.commons.InstallmentsDTO;
import com.bbva.pisd.dto.insurance.commons.PaymentPeriodDTO;
import com.bbva.pisd.dto.insurance.financing.FinancingPlanDTO;
import com.bbva.pisd.dto.insurance.policy.PaymentAmountDTO;
import com.bbva.pisd.dto.insurance.simulation.InsuranceSimulationDTO;
import com.bbva.pisd.dto.insurance.simulation.SimulationCompanyDTO;

import java.util.*;
import java.util.stream.Collectors;

public class MapperHelper {

    private final String RIMAC = "RIMAC";

    protected ApplicationConfigurationService applicationConfigurationService;

    public FinancingPlanBO createRequestRimac (FinancingPlanDTO financingPlanDTO, QuotDetailDAO quotDetailDAO) {
        FinancingPlanBO requestRimac = new FinancingPlanBO();
        FinanciamientoPayloadBO financiamientoPayloadBO = new FinanciamientoPayloadBO();
        List<CuotaFinanciamientoBO> financiamiento = financingPlanDTO.getInstallmentPlans().stream().map(installment -> createCuotaFinanciamiento(installment)).collect(Collectors.toList());
        financiamientoPayloadBO.setFinanciamiento(financiamiento);
        financiamientoPayloadBO.setCotizacion(financingPlanDTO.getQuotationId());
        financiamientoPayloadBO.setFechaInicioFinanciamiento(financingPlanDTO.getStartDate());
        requestRimac.setPayload(financiamientoPayloadBO);
        return requestRimac;
    }

    private CuotaFinanciamientoBO createCuotaFinanciamiento (InstallmentsDTO installmentsDTO) {
        CuotaFinanciamientoBO cuotaFinanciamientoBO = new CuotaFinanciamientoBO();
        cuotaFinanciamientoBO.setPeriodo( this.applicationConfigurationService.getProperty(RIMAC + installmentsDTO.getPeriod().getId()));
        return cuotaFinanciamientoBO;
    }

    public void mapSimulateInsuranceQuotationInstallmentPlanResponseValues(FinancingPlanDTO response, FinancingPlanBO responseRimac) {
        response.setQuotationId(responseRimac.getPayload().getCotizacion());
        response.setStartDate(responseRimac.getPayload().getFechaInicio());
        response.setMaturityDate(responseRimac.getPayload().getFechaFin());
        response.setTotalNumberInstallments(Long.valueOf(responseRimac.getPayload().getFinanciamiento().size()));
        List<InstallmentsDTO> installmentsDTOS = responseRimac.getPayload().getFinanciamiento().stream().map(financiamiento -> createInstallment(financiamiento)).collect(Collectors.toList());
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

    public void setApplicationConfigurationService(ApplicationConfigurationService applicationConfigurationService) {
        this.applicationConfigurationService = applicationConfigurationService;
    }
}
