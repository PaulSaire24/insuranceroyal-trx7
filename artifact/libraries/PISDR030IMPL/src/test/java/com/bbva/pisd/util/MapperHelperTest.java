package com.bbva.pisd.util;

import com.bbva.elara.configuration.manager.application.ApplicationConfigurationService;
import com.bbva.pisd.dto.insurance.aso.quotdetail.QuotDetailDAO;
import com.bbva.pisd.dto.insurance.bo.financing.CronogramaPagoBO;
import com.bbva.pisd.dto.insurance.bo.financing.CronogramaPagoLifeBO;
import com.bbva.pisd.dto.insurance.bo.financing.FinancingPlanBO;
import com.bbva.pisd.dto.insurance.commons.*;
import com.bbva.pisd.dto.insurance.dao.*;
import com.bbva.pisd.dto.insurance.financing.FinancingPlanDTO;
import com.bbva.pisd.dto.insurance.mock.MockDTO;
import com.bbva.pisd.dto.insurance.simulation.InsuranceSimulationDTO;
import com.bbva.pisd.dto.insurance.simulation.SimulationCompanyDTO;
import com.bbva.pisd.dto.insurance.simulation.VehicleDTO;
import com.bbva.pisd.lib.r030.impl.util.MapperHelper;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;

public class MapperHelperTest {

    private MapperHelper mapperHelper;
    private ApplicationConfigurationService applicationConfigurationService;
    private MockDTO mockDTO;
    private InsuranceProductDTO product;
    private InsuranceSimulationDTO insuranceSimulationDTO;
    private VehicleDTO vehicleDTO;
    private VehicleModelDTO modelDTO;
    private VehicleBrandDTO brandDTO;
    private VehiclePriceDTO priceDTO;
    private VehicleCirculationDTO vehicleCirculationDTO;

    private SimulationDAO simulationDAO;
    private SimulationProductDAO simulationProductDAO;
    private SimulationVehicleDAO simulationVehicleDAO;

    private HolderDTO holder;
    private SimulationCompanyDTO simulationCompany;
    private BankDTO bank;
    private BranchDTO branch;

    @Before
    public void setUp() {
        mapperHelper = new MapperHelper();

        applicationConfigurationService = mock(ApplicationConfigurationService.class);
        mockDTO = MockDTO.getInstance();
        product = mock(InsuranceProductDTO.class);

        insuranceSimulationDTO = mock(InsuranceSimulationDTO.class);
        vehicleDTO = mock(VehicleDTO.class);
        modelDTO = mock(VehicleModelDTO.class);
        brandDTO = mock(VehicleBrandDTO.class);
        priceDTO = mock(VehiclePriceDTO.class);
        vehicleCirculationDTO = mock(VehicleCirculationDTO.class);

        simulationDAO = mock(SimulationDAO.class);
        simulationProductDAO = mock(SimulationProductDAO.class);
        simulationVehicleDAO = mock(SimulationVehicleDAO.class);

        holder = mock(HolderDTO.class);
        simulationCompany = mock(SimulationCompanyDTO.class);
        bank = mock(BankDTO.class);
        branch = mock(BranchDTO.class);

        when(applicationConfigurationService.getProperty("RIMACMONTHLY")).thenReturn("M");
        when(applicationConfigurationService.getProperty("RIMACSEMIANNUAL")).thenReturn("R");
        when(applicationConfigurationService.getProperty("RIMACANNUAL")).thenReturn("A");
        when(applicationConfigurationService.getProperty("CUOTAMONTHLY")).thenReturn("12");
        when(applicationConfigurationService.getProperty("CUOTASEMIANNUAL")).thenReturn("2");
        when(applicationConfigurationService.getProperty("CUOTAANNUAL")).thenReturn("1");

        mapperHelper.setApplicationConfigurationService(applicationConfigurationService);
    }

    private FinancingPlanDTO requestTrxMonthlyFrquency(){
        FinancingPlanDTO financingPlanDTO = new FinancingPlanDTO();

        financingPlanDTO.setQuotationId("0814000042574");
        financingPlanDTO.setStartDate(new LocalDate());
        List<InstallmentsDTO> installmentsDTOList = new ArrayList<>();
        InstallmentsDTO installmentsDTO = new InstallmentsDTO();
        PaymentPeriodDTO paymentPeriodDTO = new PaymentPeriodDTO();
        paymentPeriodDTO.setId("MONTHLY");
        installmentsDTO.setPeriod(paymentPeriodDTO);
        installmentsDTOList.add(installmentsDTO);
        financingPlanDTO.setInstallmentPlans(installmentsDTOList);

        return financingPlanDTO;
    }

    private FinancingPlanDTO requestTrxAnnualFrequency(){
        FinancingPlanDTO financingPlanDTO = new FinancingPlanDTO();

        financingPlanDTO.setQuotationId("0814000042575");
        financingPlanDTO.setStartDate(new LocalDate());
        List<InstallmentsDTO> installmentsDTOList = new ArrayList<>();
        InstallmentsDTO installmentsDTO = new InstallmentsDTO();
        PaymentPeriodDTO paymentPeriodDTO = new PaymentPeriodDTO();
        paymentPeriodDTO.setId("ANNUAL");
        installmentsDTO.setPeriod(paymentPeriodDTO);
        installmentsDTOList.add(installmentsDTO);
        financingPlanDTO.setInstallmentPlans(installmentsDTOList);

        return financingPlanDTO;
    }

    @Test
    public void createQuoteScheduleRequestRimacLife() throws IOException{
        FinancingPlanDTO request = mockDTO.getSimulateInsuranceQuotationInstallmentPlanRequest();

        FinancingPlanBO requestRimac = mapperHelper.createRequestQuoteScheduleRimacLife(request);
        assertNotNull(requestRimac.getPayload());
    }

    @Test
    public void createQuoteScheduleRequestRimac() throws IOException{
        FinancingPlanDTO request = mockDTO.getSimulateInsuranceQuotationInstallmentPlanRequest();
        QuotDetailDAO quotDetailDAO = new QuotDetailDAO();
        quotDetailDAO.setRimacId("c9debdc9-d7e1-4464-8b3a-990c17eb9f48");
        FinancingPlanBO requestRimac = mapperHelper.createRequestQuoteScheduleRimac(request,quotDetailDAO);
        assertNotNull(requestRimac.getPayload());
    }

    @Test
    public void createPaymentScheduleRequestRimac() throws IOException{
        FinancingPlanDTO request = mockDTO.getSimulateInsuranceQuotationInstallmentPlanRequest();
        request.setStartDate(new LocalDate().plusDays(2));
        QuotDetailDAO quotDetailDAO = new QuotDetailDAO();
        quotDetailDAO.setRimacId("c9debdc9-d7e1-4464-8b3a-990c17eb9f48");
        FinancingPlanBO requestRimac = mapperHelper.createRequestPaymentScheduleRimac(request);
        assertNotNull(requestRimac.getPayload());
    }


    @Test
    public void mapSimulateInsuranceQuotationInstallmentPlanResponseValues() throws IOException {
        FinancingPlanDTO output = new FinancingPlanDTO();
        FinancingPlanBO responseRimac = mockDTO.getSimulateInsuranceQuotationInstallmentPlanResponseRimac();
        output = mapperHelper.mapSimulateInsuranceQuotationInstallmentPlanResponseValues(responseRimac);
        assertNotNull(output.getMaturityDate());
        assertNotNull(output.getMaturityDate());
        assertNotNull(output.getInstallmentPlans());
    }

    @Test
    public void mapSimulateInsuranceQuotationInstallmentPlanResponseValuesWithCronogramaPago() throws IOException {
        FinancingPlanDTO output = new FinancingPlanDTO();
        FinancingPlanDTO request = mockDTO.getSimulateInsuranceQuotationInstallmentPlanRequest();
        CronogramaPagoBO responseRimac = mockDTO.getSimulateInsuranceQuotationInstallmentPlanCronogramaPagoResponseRimac();
        responseRimac.getPayload().get(0).setPrimaBruta(new BigDecimal(584.5));
        output = mapperHelper.mapSimulateInsuranceQuotationInstallmentPlanResponseValues(request, responseRimac);
        assertNotNull(output.getInstallmentPlans());
    }


    @Test
    public void createRequestPaymentScheduleRimacLifeEasyYes_MontlyFrequency(){
        FinancingPlanDTO request = this.requestTrxMonthlyFrquency();
        FinancingPlanBO requestRimac = mapperHelper.createRequestPaymentScheduleRimacLifeEasyYes(request);

        assertNotNull(requestRimac.getPayload());
        assertNotNull(requestRimac.getPayload().getProducto());
        assertNotNull(requestRimac.getPayload().getFinanciamiento());
        assertNotNull(requestRimac.getPayload().getFinanciamiento().get(0).getFrecuencia());
        assertNotNull(requestRimac.getPayload().getFinanciamiento().get(0).getNumeroCuotas());
        assertEquals("M",requestRimac.getPayload().getFinanciamiento().get(0).getFrecuencia());
        assertEquals(new Long(12),requestRimac.getPayload().getFinanciamiento().get(0).getNumeroCuotas());
    }

    @Test
    public void createRequestPaymentScheduleRimacLifeEasyYes_AnnualFrequency(){
        FinancingPlanDTO request = this.requestTrxAnnualFrequency();
        FinancingPlanBO requestRimac = mapperHelper.createRequestPaymentScheduleRimacLifeEasyYes(request);

        assertNotNull(requestRimac.getPayload());
        assertNotNull(requestRimac.getPayload().getProducto());
        assertNotNull(requestRimac.getPayload().getFinanciamiento());
        assertNotNull(requestRimac.getPayload().getFinanciamiento().get(0).getFrecuencia());
        assertNotNull(requestRimac.getPayload().getFinanciamiento().get(0).getNumeroCuotas());
        assertEquals("A",requestRimac.getPayload().getFinanciamiento().get(0).getFrecuencia());
        assertEquals(new Long(1),requestRimac.getPayload().getFinanciamiento().get(0).getNumeroCuotas());
    }


    @Test
    public void mapSimulatePaymentScheduleLifeEasyYesResponse_MonthlyFrequency() throws IOException{
        FinancingPlanDTO request = this.requestTrxMonthlyFrquency();
        CronogramaPagoLifeBO responseRimac = mockDTO.getSimulateInsuranceQuotationInstallmentPlanCronogramaPagoResponseRimacLifeEasyYes();

        FinancingPlanDTO output = mapperHelper.mapSimulatePaymentScheduleLifeEasyYesResponse(request, responseRimac);
        assertNotNull(output.getStartDate());
        assertNotNull(output.getMaturityDate());
        assertNotNull(output.getInstallmentPlans());

    }


}