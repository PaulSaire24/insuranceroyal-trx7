package com.bbva.pisd.lib.r030.impl.util;

import com.bbva.elara.configuration.manager.application.ApplicationConfigurationService;
import com.bbva.pisd.dto.insurance.aso.quotdetail.QuotDetailDAO;
import com.bbva.pisd.dto.insurance.bo.financing.CronogramaPagoBO;
import com.bbva.pisd.dto.insurance.bo.financing.FinanciamientoBO;
import com.bbva.pisd.dto.insurance.bo.financing.FinanciamientoPayloadBO;
import com.bbva.pisd.dto.insurance.bo.financing.FinancingPlanBO;
import com.bbva.pisd.dto.insurance.commons.InstallmentsDTO;
import com.bbva.pisd.dto.insurance.commons.PaymentPeriodDTO;
import com.bbva.pisd.dto.insurance.financing.FinancingPlanDTO;
import com.bbva.pisd.dto.insurance.policy.PaymentAmountDTO;
import org.joda.time.LocalDate;

import java.util.List;
import java.util.stream.Collectors;

public class MapperHelper {

    private final String RIMAC = "RIMAC";
    private final String CUOTA = "CUOTA";

    protected ApplicationConfigurationService applicationConfigurationService;

    public FinancingPlanBO createRequestQuoteScheduleRimac (FinancingPlanDTO financingPlanDTO, QuotDetailDAO quotDetailDAO) {
        FinancingPlanBO requestRimac = new FinancingPlanBO();
        FinanciamientoPayloadBO financiamientoPayloadBO = new FinanciamientoPayloadBO();
        List<FinanciamientoBO> financiamiento = financingPlanDTO.getInstallmentPlans().stream().map(installment -> createCuotaFinanciamiento(installment)).collect(Collectors.toList());
        financiamientoPayloadBO.setFinanciamiento(financiamiento);
        financiamientoPayloadBO.setCotizacion(quotDetailDAO.getRimacId());
        financiamientoPayloadBO.setFechaInicioFinanciamiento(financingPlanDTO.getStartDate());
        requestRimac.setPayload(financiamientoPayloadBO);
        return requestRimac;
    }

    public FinancingPlanBO createRequestPaymentScheduleRimac (FinancingPlanDTO financingPlanDTO) {
        FinancingPlanBO requestRimac = new FinancingPlanBO();
        FinanciamientoPayloadBO financiamientoPayloadBO = new FinanciamientoPayloadBO();
        List<FinanciamientoBO> financiamiento = financingPlanDTO.getInstallmentPlans().stream().map(installment -> createCuotaFinanciamiento(installment, financingPlanDTO)).collect(Collectors.toList());
        financiamientoPayloadBO.setFinanciamiento(financiamiento);
        requestRimac.setPayload(financiamientoPayloadBO);
        return requestRimac;
    }

    private FinanciamientoBO createCuotaFinanciamiento (InstallmentsDTO installmentsDTO, FinancingPlanDTO financingPlanDTO) {
        FinanciamientoBO financiamientoBO = new FinanciamientoBO();
        String frecuencia =  this.applicationConfigurationService.getProperty(RIMAC + installmentsDTO.getPeriod().getId());
        String numeroCuotas =  this.applicationConfigurationService.getProperty(CUOTA + installmentsDTO.getPeriod().getId());
        financiamientoBO.setFrecuencia(frecuencia);
        financiamientoBO.setNumeroCuotas(Long.parseLong(numeroCuotas));
        financiamientoBO.setFechaInicio(financingPlanDTO.getStartDate());
        return financiamientoBO;
    }

    private FinanciamientoBO createCuotaFinanciamiento (InstallmentsDTO installmentsDTO) {
        FinanciamientoBO financiamientoBO = new FinanciamientoBO();
        String periodoId =  this.applicationConfigurationService.getProperty(RIMAC + installmentsDTO.getPeriod().getId());
        String nroCuotas =  this.applicationConfigurationService.getProperty(CUOTA + installmentsDTO.getPeriod().getId());
        financiamientoBO.setPeriodo(periodoId);
        financiamientoBO.setNroCuotas(Long.parseLong(nroCuotas));
        return financiamientoBO;
    }
    public FinancingPlanDTO mapSimulateInsuranceQuotationInstallmentPlanResponseValues(FinancingPlanBO responseRimac) {
        FinancingPlanDTO response = new FinancingPlanDTO();
        response.setStartDate(responseRimac.getPayload().getFechaInicio());
        response.setMaturityDate(responseRimac.getPayload().getFechaFin());
        List<InstallmentsDTO> installmentsDTOS = responseRimac.getPayload().getFinanciamiento().stream().map(financiamiento -> createInstallment(financiamiento)).collect(Collectors.toList());
        response.setInstallmentPlans(installmentsDTOS);
        return response;
    }

    public FinancingPlanDTO mapSimulateInsuranceQuotationInstallmentPlanResponseValues(FinancingPlanDTO request, CronogramaPagoBO responseRimac) {
        FinancingPlanDTO response = new FinancingPlanDTO();
        response.setStartDate(new LocalDate(responseRimac.getPayload().get(0).getFechaInicio()));
        response.setMaturityDate(new LocalDate(responseRimac.getPayload().get(0).getFechaFinal()));
        List<InstallmentsDTO> installmentsDTOS = responseRimac.getPayload().stream().map(cronogramaPago -> createInstallment(cronogramaPago, request)).collect(Collectors.toList());
        response.setInstallmentPlans(installmentsDTOS);
        return response;
    }

    private InstallmentsDTO createInstallment (com.bbva.pisd.dto.insurance.bo.FinanciamientoBO financiamientoBO, FinancingPlanDTO request) {
        InstallmentsDTO installmentsDTO = new InstallmentsDTO();
        PaymentPeriodDTO period = new PaymentPeriodDTO();
        PaymentAmountDTO amount = new PaymentAmountDTO();
        amount.setAmount(0.0);
        amount.setCurrency("USD");
        period.setId(request.getInstallmentPlans().get(0).getPeriod().getId());
        installmentsDTO.setPeriod(period);
        installmentsDTO.setPaymentAmount(amount);
        return installmentsDTO;
    }

    private InstallmentsDTO createInstallment (FinanciamientoBO financiamiento) {
        InstallmentsDTO installmentsDTO = new InstallmentsDTO();
        PaymentPeriodDTO period = new PaymentPeriodDTO();
        PaymentAmountDTO amount = new PaymentAmountDTO();
        amount.setAmount(financiamiento.getMontoCuota().doubleValue());
        amount.setCurrency(financiamiento.getMoneda());
        String periodicidad = financiamiento.getDescripcionPeriodo();
        period.setId(this.applicationConfigurationService.getProperty(periodicidad));
        period.setName(periodicidad);
        installmentsDTO.setTotalNumberInstallments(financiamiento.getNumeroCuotasTotales());
        installmentsDTO.setPeriod(period);
        installmentsDTO.setPaymentAmount(amount);
        return installmentsDTO;
    }

    public void setApplicationConfigurationService(ApplicationConfigurationService applicationConfigurationService) {
        this.applicationConfigurationService = applicationConfigurationService;
    }
}
