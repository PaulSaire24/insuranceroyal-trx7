package com.bbva.pisd.lib.r030.impl.util;

import com.bbva.elara.configuration.manager.application.ApplicationConfigurationService;
import com.bbva.pisd.dto.insurance.bo.financing.CronogramaPagoBO;
import com.bbva.pisd.dto.insurance.bo.financing.FinanciamientoBO;
import com.bbva.pisd.dto.insurance.bo.financing.FinancingPlanBO;
import com.bbva.pisd.dto.insurance.bo.financing.CronogramaPagoLifeBO;
import com.bbva.pisd.dto.insurance.commons.InstallmentsDTO;
import com.bbva.pisd.dto.insurance.commons.PaymentPeriodDTO;
import com.bbva.pisd.dto.insurance.financing.FinancingPlanDTO;
import com.bbva.pisd.dto.insurance.policy.PaymentAmountDTO;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class MapperHelper {

    protected ApplicationConfigurationService applicationConfigurationService;


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
        DateTimeZone dateTimeZone = DateTimeZone.forID("GMT");
        response.setStartDate(new LocalDate(responseRimac.getPayload().get(0).getFechaInicio(), dateTimeZone));
        response.setMaturityDate(new LocalDate(responseRimac.getPayload().get(0).getFechaFinal(), dateTimeZone));
        List<InstallmentsDTO> installmentsDTOS = responseRimac.getPayload().stream().map(cronogramaPago -> createInstallment(cronogramaPago, request)).collect(Collectors.toList());
        response.setInstallmentPlans(installmentsDTOS);
        return response;
    }

    public FinancingPlanDTO mapSimulatePaymentScheduleLifeEasyYesResponse(FinancingPlanDTO request, CronogramaPagoLifeBO responseRimac) {
        FinancingPlanDTO response = new FinancingPlanDTO();
        DateTimeZone dateTimeZone = DateTimeZone.forID("GMT");

        response.setStartDate(Objects.nonNull(responseRimac.getPayload().getFechaInicio()) ? new LocalDate(responseRimac.getPayload().getFechaInicio(),dateTimeZone) : new LocalDate(dateTimeZone));
        response.setMaturityDate(Objects.nonNull(responseRimac.getPayload().getFechaFinal()) ? new LocalDate(responseRimac.getPayload().getFechaFinal(),dateTimeZone) : new LocalDate(dateTimeZone));
        List<InstallmentsDTO> installmentsDTOS = new ArrayList<>();
        InstallmentsDTO installmentsDTO = new InstallmentsDTO();

        String totalNumberInstallments = this.applicationConfigurationService.getProperty(Constants.CUOTA + request.getInstallmentPlans().get(0).getPeriod().getId());
        installmentsDTO.setTotalNumberInstallments(Long.parseLong(totalNumberInstallments));

        PaymentPeriodDTO period = new PaymentPeriodDTO();
        period.setId(request.getInstallmentPlans().get(0).getPeriod().getId());
        installmentsDTO.setPeriod(period);

        PaymentAmountDTO amount = new PaymentAmountDTO();
        amount.setAmount(responseRimac.getPayload().getPrimaBruta().doubleValue());
        amount.setCurrency("PEN");
        installmentsDTO.setPaymentAmount(amount);

        installmentsDTOS.add(installmentsDTO);
        response.setInstallmentPlans(installmentsDTOS);

        return response;
    }

    private InstallmentsDTO createInstallment (com.bbva.pisd.dto.insurance.bo.FinanciamientoBO financiamientoBO, FinancingPlanDTO request) {
        InstallmentsDTO installmentsDTO = new InstallmentsDTO();
        PaymentPeriodDTO period = new PaymentPeriodDTO();
        PaymentAmountDTO amount = new PaymentAmountDTO();
        amount.setAmount(financiamientoBO.getPrimaBruta().doubleValue());
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
