package com.bbva.pisd.util;

import com.bbva.elara.configuration.manager.application.ApplicationConfigurationService;
import com.bbva.pisd.dto.insurance.aso.quotdetail.QuotDetailDAO;
import com.bbva.pisd.dto.insurance.bo.financing.CronogramaPagoBO;
import com.bbva.pisd.dto.insurance.bo.financing.FinancingPlanBO;
import com.bbva.pisd.dto.insurance.commons.*;
import com.bbva.pisd.dto.insurance.dao.*;
import com.bbva.pisd.dto.insurance.financing.FinancingPlanDTO;
import com.bbva.pisd.dto.insurance.mock.MockDTO;
import com.bbva.pisd.dto.insurance.simulation.InsuranceSimulationDTO;
import com.bbva.pisd.dto.insurance.simulation.SimulationCompanyDTO;
import com.bbva.pisd.dto.insurance.simulation.VehicleDTO;
import com.bbva.pisd.lib.r030.impl.util.MapperHelper;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class MapperHelperTest {

    private MapperHelper mapperHelper;
    private ApplicationConfigurationService applicationConfigurationService;
    private MockDTO mockDTO;
    private InsuranceProductDTO product;
    private InsuranceProductDAO productDAO;

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
        productDAO = mock(InsuranceProductDAO.class);

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
        when(applicationConfigurationService.getProperty("CUOTAMONTHLY")).thenReturn("12");
        when(applicationConfigurationService.getProperty("CUOTASEMIANNUAL")).thenReturn("2");

        mapperHelper.setApplicationConfigurationService(applicationConfigurationService);
    }

    @Test
    public void createRequestRimac() throws IOException{
        FinancingPlanDTO request = mockDTO.getSimulateInsuranceQuotationInstallmentPlanRequest();
        QuotDetailDAO quotDetailDAO = new QuotDetailDAO();
        quotDetailDAO.setRimacId("c9debdc9-d7e1-4464-8b3a-990c17eb9f48");
        FinancingPlanBO requestRimac = mapperHelper.createRequestQuoteScheduleRimac(request,quotDetailDAO);
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
        output = mapperHelper.mapSimulateInsuranceQuotationInstallmentPlanResponseValues(request, responseRimac);
        assertNotNull(output.getInstallmentPlans());
    }
}
