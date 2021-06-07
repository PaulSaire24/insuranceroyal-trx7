package com.bbva.pisd.lib.r015.impl.util;

import com.bbva.elara.configuration.manager.application.ApplicationConfigurationService;

import com.bbva.pisd.dto.insurance.aso.tier.TierASO;

import com.bbva.pisd.dto.insurance.bo.SimulacionPayloadBO;
import com.bbva.pisd.dto.insurance.bo.DatoParticularBO;
import com.bbva.pisd.dto.insurance.bo.AseguradoBO;
import com.bbva.pisd.dto.insurance.bo.CotizacionBO;
import com.bbva.pisd.dto.insurance.bo.CuotaFinanciamientoBO;

import com.bbva.pisd.dto.insurance.bo.simulation.InsuranceSimulationBO;

import com.bbva.pisd.dto.insurance.commons.TierDTO;
import com.bbva.pisd.dto.insurance.commons.InsuranceProductDTO;
import com.bbva.pisd.dto.insurance.commons.InsuranceProductModalityDTO;
import com.bbva.pisd.dto.insurance.commons.CoverageModalityDTO;
import com.bbva.pisd.dto.insurance.commons.BenefitModalityDTO;
import com.bbva.pisd.dto.insurance.commons.ExclusionDTO;
import com.bbva.pisd.dto.insurance.commons.DeductibleDTO;
import com.bbva.pisd.dto.insurance.commons.VehicleModelDTO;
import com.bbva.pisd.dto.insurance.commons.UnitDTO;
import com.bbva.pisd.dto.insurance.commons.InstallmentModalityDTO;
import com.bbva.pisd.dto.insurance.commons.PaymentPeriodDTO;
import com.bbva.pisd.dto.insurance.commons.InsurancePrimeAmountDTO;
import com.bbva.pisd.dto.insurance.commons.InsuranceFinancingDTO;
import com.bbva.pisd.dto.insurance.commons.InstallmentsDTO;
import com.bbva.pisd.dto.insurance.commons.BillAmountDTO;

import com.bbva.pisd.dto.insurance.dao.InsuranceProductDAO;
import com.bbva.pisd.dto.insurance.dao.InsuranceProductModalityDAO;
import com.bbva.pisd.dto.insurance.dao.ConsiderationsDAO;
import com.bbva.pisd.dto.insurance.dao.SimulationDAO;
import com.bbva.pisd.dto.insurance.dao.SimulationProductDAO;
import com.bbva.pisd.dto.insurance.dao.SimulationVehicleDAO;

import com.bbva.pisd.dto.insurance.simulation.InsuranceSimulationDTO;

import com.bbva.pisd.dto.insurance.simulation.SimulationCompanyDTO;
import com.bbva.pisd.dto.insurance.utils.PISDProperties;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;

import java.util.List;
import java.util.ArrayList;
import java.util.Objects;

import java.util.Map;
import java.util.HashMap;
import java.util.Date;
import java.util.stream.Collectors;

public class MapperHelper {

    protected ApplicationConfigurationService applicationConfigurationService;

    private static final String MODELO_VEHICULOS = "MODELOS_DE_VEHICULOS";
    private static final String MARCA_VEHICULOS = "MARCAS_DE_VEHICULOS";
    private static final String ANIO_FABRICACION = "WEB_ANOS_DE_FABRICACION";
    private static final String PLACA_RODAJE = "PLACA_DE_RODAJE";
    private static final String FACTOR_BANCARIO = "FACTOR BANCARIO";
    private static final String UBICACION = "UBICACION";
    private static final String FACTOR_CAMPANIA = "FACTOR CAMPANA";
    private static final String SUMA_ASEGURADA = "SUMA_ASEGURADA";
    private static final String COMBUSTIBLE_GAS = "COMBUSTIBLE_GAS";
    private static final String TIER = "TIER";

    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    private String[] datos;

    public MapperHelper() {
        datos = new String[]{PLACA_RODAJE, MARCA_VEHICULOS, MODELO_VEHICULOS, ANIO_FABRICACION,
                SUMA_ASEGURADA, UBICACION, FACTOR_CAMPANIA, TIER, FACTOR_BANCARIO, COMBUSTIBLE_GAS};
    }

    public void mappingTierASO(InsuranceSimulationDTO insuranceSimulationDto, TierASO responseTierASO) {
        if (Objects.nonNull(responseTierASO)) {
            TierDTO tierDTO = new TierDTO();
            tierDTO.setId(responseTierASO.getData().get(0).getId());
            tierDTO.setName(responseTierASO.getData().get(0).getDescription());
            insuranceSimulationDto.setTier(tierDTO);
            insuranceSimulationDto.setBankingFactor(responseTierASO.getData().get(0).getChargeFactor());
        }
    }

    public InsuranceSimulationBO mapInRequestSimulation(InsuranceSimulationDTO request) {
        InsuranceSimulationBO rimacRequest = new InsuranceSimulationBO();

        SimulacionPayloadBO payload = new SimulacionPayloadBO();

        payload.setMoneda(request.getVehicle().getPrice().getCurrency());

        List<DatoParticularBO> datosParticulares = getDatosParticulares(datos, request);
        payload.setDatosParticulares(datosParticulares);

        AseguradoBO asegurado = new AseguradoBO();
        asegurado.setTipoDocumento(request.getHolder().getIdentityDocument().getDocumentType().getId());
        asegurado.setNumeroDocumento(request.getHolder().getIdentityDocument().getDocumentNumber());
        payload.setAsegurado(asegurado);

        rimacRequest.setPayload(payload);
        return rimacRequest;
    }

    private List<DatoParticularBO> getDatosParticulares(String[] datos, InsuranceSimulationDTO request) {

        List<DatoParticularBO> datosParticulares = new ArrayList<>();

        for(String dato : datos) {
            DatoParticularBO datoParticular = new DatoParticularBO();
            datoParticular.setEtiqueta(dato);
            switch(dato) {
                case PLACA_RODAJE:
                    datoParticular.setCodigo("");
                    datoParticular.setValor(validateVehicleLicence(request.getVehicle().getLicensePlate()));
                    break;
                case MARCA_VEHICULOS:
                    datoParticular.setCodigo(request.getVehicle().getModel().getBrand().getId());
                    datoParticular.setValor("");
                    break;
                case MODELO_VEHICULOS:
                    datoParticular.setCodigo(request.getVehicle().getModel().getId());
                    datoParticular.setValor("");
                    break;
                case ANIO_FABRICACION:
                    datoParticular.setCodigo("");
                    datoParticular.setValor(String.valueOf(request.getVehicle().getModel().getYear()));
                    break;
                case SUMA_ASEGURADA:
                    datoParticular.setCodigo("");
                    datoParticular.setValor(String.valueOf(request.getVehicle().getPrice().getAmount()));
                    break;
                case UBICACION:
                    datoParticular.setCodigo("");
                    datoParticular.setValor(request.getVehicleCirculation().getId().equals("L") ? "LIMA" : "PROVINCIA");
                    break;
                case FACTOR_CAMPANIA:
                    datoParticular.setCodigo("");
                    datoParticular.setValor(validateCampaignFactor(request.getCampaignFactor()));
                    break;
                case TIER:
                    datoParticular.setCodigo("");
                    datoParticular.setValor(validateTier(request.getTier()));
                    break;
                case FACTOR_BANCARIO:
                    datoParticular.setCodigo("");
                    datoParticular.setValor(validateBankingFactor(request.getBankingFactor()));
                    break;
                case COMBUSTIBLE_GAS:
                    datoParticular.setCodigo("");
                    datoParticular.setValor(request.getVehicle().getHasPowerSupplyConvertion().equals("S") ? "SI" : "NO");
                    break;
                default:
                    break;
            }
            if(Objects.nonNull(datoParticular.getValor())) { datosParticulares.add(datoParticular); }
        }
        return datosParticulares;
    }

    private String validateVehicleLicence(String vehicleLicence) {
        return Objects.nonNull(vehicleLicence) ? vehicleLicence : null;
    }

    private String validateCampaignFactor(Double campaignFactor) {
        return Objects.nonNull(campaignFactor) ? String.valueOf(campaignFactor) : null;
    }

    private String validateTier(TierDTO tier) {
        return (Objects.nonNull(tier) && !tier.getId().equals("0000")) ? tier.getId() : null;
    }

    private String validateBankingFactor(Double bankingFactor) {
        
        if(Objects.isNull(bankingFactor) || bankingFactor == 0.0) {
            return String.valueOf(1);
        }

        return String.valueOf((bankingFactor/100));
        
    }

    public void mapSimulationRimacValues(InsuranceSimulationDTO response, InsuranceSimulationBO responseRimac) {

        SimulationCompanyDTO simulationCompany = new SimulationCompanyDTO();
        simulationCompany.setId(responseRimac.getPayload().getCotizaciones().get(0).getIdCotizacion());

        response.setSimulationCompany(simulationCompany);
        response.setValidityDays(Long.parseLong(responseRimac.getPayload().getCotizaciones().get(0).getDiasVigencia()));
        response.setMaturityDate(responseRimac.getPayload().getCotizaciones().get(0).getFechaFinVigencia());

        String gpsRequired = responseRimac.getPayload().getCotizaciones().get(0).getPlan().getIndicadorGPS().equals("SI") ? "S" : "N";
        response.setIsGpsRequired(gpsRequired);
        response.setIsInspectionRequired(responseRimac.getPayload().getCotizaciones().get(0).getPlan().getIndicadorInspeccion());

        for(DatoParticularBO datoParticular : responseRimac.getPayload().getDatosParticulares()) {
            if(Objects.nonNull(response.getVehicle().getModel().getName()) && Objects.nonNull(response.getVehicle().getModel().getBrand().getName())) { break; }
            setModelAndBrandNames(datoParticular, response.getVehicle().getModel());
        }
    }

    public Map<String, Object> insuranceProductFiltersCreation(InsuranceProductDTO product) {
        Map<String, Object> filters = new HashMap<>();
        filters.put(PISDProperties.FILTER_INSURANCE_PRODUCT_TYPE.getValue(), product.getId());
        return filters;
    }

    public Map<String, Object> insuranceBusinessFiltersCreation(InsuranceProductDAO insuranceProductDAO) {
        Map<String, Object> filters = new HashMap<>();
        filters.put(PISDProperties.FIELD_OR_FILTER_INSURANCE_RISK_BUSINESS_ID.getValue(), insuranceProductDAO.getInsuranceRiskBusinessId());
        return filters;
    }

    public Map<String, Object> insuranceProductModalityFiltersCreation(List<InsuranceProductModalityDTO> plans, Long insuranceProductId) {
        Map<String, Object> filters = new HashMap<>();

        List<String> planes = new ArrayList<>();

        plans.forEach(plan -> planes.add(plan.getId()));

        filters.put(PISDProperties.FIELD_OR_FILTER_INSURANCE_PRODUCT_ID.getValue(), insuranceProductId);
        filters.put(PISDProperties.FIELD_OR_FILTER_INSURANCE_MODALITY_TYPE.getValue(), planes);
        return filters;
    }

    public List<Long> getPlanesRimac(List<InsuranceProductModalityDAO> productModalities) {
        return productModalities.stream().map(modality -> Long.parseLong(modality.getInsuranceCompanyModalityId()))
                .collect(Collectors.toList());
    }

    public void putConsiderations(List<InsuranceProductModalityDTO> plans, List<ConsiderationsDAO> considerationsDAO) {
        String id = null;
        String name = null;
        String considerationType = null;

        for(InsuranceProductModalityDTO plan : plans) {

            List<ConsiderationsDAO> considerationsForThisPlan = new ArrayList<>();

            List<CoverageModalityDTO> coverages = new ArrayList<>();
            List<BenefitModalityDTO> benefits = new ArrayList<>();
            List<ExclusionDTO> exclusions = new ArrayList<>();
            List<DeductibleDTO> deductibles = new ArrayList<>();

            for(ConsiderationsDAO consideration : considerationsDAO) {
                if(plan.getId().equals(consideration.getInsuranceModalityType())) {

                    id = String.valueOf(consideration.getIdConsideration());
                    name = consideration.getInsuranceConsiderationDescription();
                    considerationType = consideration.getConsiderationType();

                    switch (considerationType) {
                        case "CO":
                        case "CB":
                            coverages.add(createCoverage(id, name, considerationType, consideration));
                            break;
                        case "BE":
                            benefits.add(createBenefit(id, name, consideration));
                            break;
                        case "EX":
                        case "VN":
                            exclusions.add(createExclusion(id, name, considerationType, consideration));
                            break;
                        case "DE":
                            deductibles.add(createDeductible(id, name));
                            break;
                        default:
                            break;
                    }
                    considerationsForThisPlan.add(consideration);
                }
            }
            plan.setCoverages(coverages);
            plan.setBenefits(benefits);
            plan.setExclusions(exclusions);
            plan.setDeductibles(deductibles);

            considerationsDAO.removeAll(considerationsForThisPlan);
        }
    }

    private CoverageModalityDTO createCoverage(String id, String name, String considerationType, ConsiderationsDAO consideration) {
        CoverageModalityDTO coverage = new CoverageModalityDTO();
        coverage.setId(considerationType.equals("CB") ? getThousandId(id) : id);
        coverage.setName(name);
        coverage.setUnit(getConsiderationUnit(consideration));
        return coverage;
    }

    private BenefitModalityDTO createBenefit(String id, String name, ConsiderationsDAO consideration) {
        BenefitModalityDTO benefit = new BenefitModalityDTO();
        benefit.setId(id);
        benefit.setName(name);
        benefit.setUnit(getConsiderationUnit(consideration));
        return benefit;
    }

    private ExclusionDTO createExclusion(String id, String name, String considerationType, ConsiderationsDAO consideration) {
        ExclusionDTO exclusion = new ExclusionDTO();
        exclusion.setId(considerationType.equals("VN") ? getThousandId(id) : id);
        exclusion.setName(name);
        exclusion.setUnit(getConsiderationUnit(consideration));
        return exclusion;
    }

    private DeductibleDTO createDeductible(String id, String name) {
        DeductibleDTO deductible = new DeductibleDTO();
        deductible.setId(id);
        deductible.setName(name);
        return deductible;
    }

    private UnitDTO getConsiderationUnit(ConsiderationsDAO consideration) {
        UnitDTO unit = new UnitDTO();
        unit.setUnitType(this.applicationConfigurationService.getProperty(consideration.getConsiderationValueType()));
        unit.setPercentage(consideration.getConsiderationPercentage());
        unit.setAmount(consideration.getConsiderationAmount());
        unit.setCurrency(Objects.nonNull(consideration.getConsiderationCurrency()) ? consideration.getConsiderationCurrency() : "");
        unit.setText("");
        unit.setDecimal(0.0);
        return unit;
    }

    private String getThousandId(String id) {
        Long initialValue = 1000L;
        return String.valueOf(initialValue + Long.parseLong(id));
    }

    public List<InsuranceProductModalityDTO> getPlansNamesAndRecommendedValuesAndInstallmentsPlans(List<InsuranceProductModalityDAO> productModalities, InsuranceSimulationBO responseRimac) {
        List<InsuranceProductModalityDTO> plans = new ArrayList<>();

        Long paymentsTotalNumber = responseRimac.getPayload().getFinanciamiento().get(0).getNumeroCuotas();

        for(InsuranceProductModalityDAO productModality : productModalities) {
            for(CotizacionBO cotizacion : responseRimac.getPayload().getCotizaciones()) {
                if(productModality.getInsuranceCompanyModalityId().equals(String.valueOf(cotizacion.getPlan().getIdPlan()))) {

                    InsuranceProductModalityDTO plan = new InsuranceProductModalityDTO();
                    InstallmentModalityDTO installmentPlan = new InstallmentModalityDTO();

                    plan.setId(productModality.getInsuranceModalityType());
                    plan.setName(productModality.getInsuranceModalityName());
                    plan.setIsRecommended(productModality.getSuggestedModalityIndType());

                    PaymentPeriodDTO period = new PaymentPeriodDTO();

                    String periodicidad = cotizacion.getPlan().getFinanciamientos().get(0).getPeriodicidad();

                    period.setId(this.applicationConfigurationService.getProperty(periodicidad));
                    period.setName(periodicidad);

                    InsurancePrimeAmountDTO amount = new InsurancePrimeAmountDTO();
                    amount.setAmount(cotizacion.getPlan().getPrimaBruta().doubleValue());
                    amount.setCurrency(cotizacion.getPlan().getMoneda());

                    InsuranceFinancingDTO financing = new InsuranceFinancingDTO();

                    financing.setPaymentsTotalnumber(paymentsTotalNumber);
                    financing.setStartDate(cotizacion.getPlan().getFinanciamientos().get(0).getFechaInicio());
                    financing.setMaturityDate(cotizacion.getPlan().getFinanciamientos().get(0).getFechaFinal());

                    List<InstallmentsDTO> installments = getInstallments(cotizacion.getPlan().getFinanciamientos().get(0).getCuotasFinanciamiento());

                    financing.setInstallments(installments);

                    installmentPlan.setPeriod(period);
                    installmentPlan.setAmount(amount);
                    installmentPlan.setFinancing(financing);

                    plan.setInstallmentPlan(installmentPlan);

                    plans.add(plan);
                }
            }
        }
        return plans;
    }

    private List<InstallmentsDTO> getInstallments(List<CuotaFinanciamientoBO> cuotasFinanciamiento) {
        return cuotasFinanciamiento.stream().map(cuotaFinanciamiento -> createInstallment(cuotaFinanciamiento)).collect(Collectors.toList());
    }

    private InstallmentsDTO createInstallment(CuotaFinanciamientoBO cuotaFinanciamiento) {
        InstallmentsDTO installment = new InstallmentsDTO();
        installment.setPaymentNumber(cuotaFinanciamiento.getCuota());

        BillAmountDTO installmentAmount = new BillAmountDTO();
        installmentAmount.setAmount(cuotaFinanciamiento.getMonto().doubleValue());
        installmentAmount.setCurrency(cuotaFinanciamiento.getMoneda());
        installment.setAmount(installmentAmount);

        installment.setMaturityDate(cuotaFinanciamiento.getFechaVencimiento());
        return installment;
    }

    public void putNamesAndRecommendedValuesAndInstallmentsPlan(InsuranceSimulationDTO response, List<InsuranceProductModalityDTO> plansWithInstallmentPlan) {
        for(InsuranceProductModalityDTO planWithoutNameAndRecommendedValueAndInstallmentPlan : response.getProduct().getPlans()) {
            for(InsuranceProductModalityDTO fullPlan : plansWithInstallmentPlan) {
                if(planWithoutNameAndRecommendedValueAndInstallmentPlan.getId().equals(fullPlan.getId())) {
                    planWithoutNameAndRecommendedValueAndInstallmentPlan.setName(fullPlan.getName());
                    planWithoutNameAndRecommendedValueAndInstallmentPlan.setIsRecommended(fullPlan.getIsRecommended());
                    planWithoutNameAndRecommendedValueAndInstallmentPlan.setInstallmentPlan(fullPlan.getInstallmentPlan());
                }
            }
        }
    }

    public SimulationDAO createSimulationDAO(BigDecimal insuranceSimulationId, String factorType, Double chargeFactor, InsuranceSimulationDTO insuranceSimulationDTO) {
        SimulationDAO simulationDAO = new SimulationDAO();
        simulationDAO.setInsuranceSimulationId(insuranceSimulationId);
        simulationDAO.setInsrncCompanySimulationId(insuranceSimulationDTO.getSimulationCompany().getId());
        simulationDAO.setCustomerId(insuranceSimulationDTO.getHolder().getId());
        simulationDAO.setCustomerSimulationDate(dateFormat.format(new Date()));
        simulationDAO.setCustSimulationExpiredDate(dateFormat.format(insuranceSimulationDTO.getMaturityDate()));
        if(Objects.nonNull(factorType)) {
            simulationDAO.setBankFactorType(factorType);
            switch (factorType) {
                case "C":
                    simulationDAO.setBankFactorPer(BigDecimal.valueOf(chargeFactor/100));
                    simulationDAO.setBankFactorAmount(BigDecimal.valueOf(0));
                    break;
                case "F":
                    simulationDAO.setBankFactorPer(BigDecimal.valueOf(0));
                    simulationDAO.setBankFactorAmount(BigDecimal.valueOf(chargeFactor));
                    break;
                case "D":
                    simulationDAO.setBankFactorPer(BigDecimal.valueOf(0));
                    simulationDAO.setBankFactorAmount(BigDecimal.valueOf(0));
                    break;
                default:
                    break;
            }
        }
        simulationDAO.setSourceBranchId(insuranceSimulationDTO.getBank().getBranch().getId());
        return simulationDAO;
    }

    public Map<String, Object> createArgumentsForSaveSimulation(SimulationDAO simulationDAO, String creationUser, String userAudit) {
        Map<String, Object> arguments = new HashMap<>();
        arguments.put(PISDProperties.FIELD_INSURANCE_SIMULATION_ID.getValue(), simulationDAO.getInsuranceSimulationId());
        arguments.put(PISDProperties.FIELD_INSRNC_COMPANY_SIMULATION_ID.getValue(), simulationDAO.getInsrncCompanySimulationId());
        arguments.put(PISDProperties.FIELD_CUSTOMER_ID.getValue(), simulationDAO.getCustomerId());
        arguments.put(PISDProperties.FIELD_CUSTOMER_SIMULATION_DATE.getValue(), simulationDAO.getCustomerSimulationDate());
        arguments.put(PISDProperties.FIELD_CUST_SIMULATION_EXPIRED_DATE.getValue(), simulationDAO.getCustSimulationExpiredDate());
        arguments.put(PISDProperties.FIELD_BANK_FACTOR_TYPE.getValue(), simulationDAO.getBankFactorType());
        arguments.put(PISDProperties.FIELD_BANK_FACTOR_AMOUNT.getValue(), simulationDAO.getBankFactorAmount());
        arguments.put(PISDProperties.FIELD_BANK_FACTOR_PER.getValue(), simulationDAO.getBankFactorPer());
        arguments.put(PISDProperties.FIELD_SOURCE_BRANCH_ID.getValue(), simulationDAO.getSourceBranchId());
        arguments.put(PISDProperties.FIELD_CREATION_USER_ID.getValue(), creationUser);
        arguments.put(PISDProperties.FIELD_USER_AUDIT_ID.getValue(), userAudit);
        return arguments;
    }

    public SimulationProductDAO createSimulationProductDAO(BigDecimal insuranceSimulationId, Long productId, String creationUser, String userAudit, InsuranceSimulationDTO insuranceSimulationDto) {
        SimulationProductDAO simulationProductDAO = new SimulationProductDAO();
        simulationProductDAO.setInsuranceSimulationId(insuranceSimulationId);
        simulationProductDAO.setInsuranceProductId(new BigDecimal(productId));
        simulationProductDAO.setSaleChannelId(insuranceSimulationDto.getSaleChannelId());
        simulationProductDAO.setSourceBranchId(insuranceSimulationDto.getBank().getBranch().getId());
        simulationProductDAO.setCreationUser(creationUser);
        simulationProductDAO.setUserAudit(userAudit);
        return simulationProductDAO;
    }

    public Map<String, Object> createArgumentsForSaveSimulationProduct(SimulationProductDAO simulationProductDAO) {
        Map<String, Object> arguments = new HashMap<>();
        arguments.put(PISDProperties.FIELD_INSURANCE_SIMULATION_ID.getValue(), simulationProductDAO.getInsuranceSimulationId());
        arguments.put(PISDProperties.FIELD_OR_FILTER_INSURANCE_PRODUCT_ID.getValue(), simulationProductDAO.getInsuranceProductId());
        arguments.put(PISDProperties.FIELD_CAMPAIGN_FACTOR_TYPE.getValue(), simulationProductDAO.getCampaignFactorType());
        arguments.put(PISDProperties.FIELD_CAMPAIGN_OFFER_1_AMOUNT.getValue(), simulationProductDAO.getCampaignOffer1Amount());
        arguments.put(PISDProperties.FIELD_CAMPAIGN_FACTOR_PER.getValue(), simulationProductDAO.getCampaignFactorPer());
        arguments.put(PISDProperties.FIELD_SALE_CHANNEL_ID.getValue(), simulationProductDAO.getSaleChannelId());
        arguments.put(PISDProperties.FIELD_SOURCE_BRANCH_ID.getValue(), simulationProductDAO.getSourceBranchId());
        arguments.put(PISDProperties.FIELD_CREATION_USER_ID.getValue(), simulationProductDAO.getCreationUser());
        arguments.put(PISDProperties.FIELD_USER_AUDIT_ID.getValue(), simulationProductDAO.getUserAudit());
        return arguments;
    }

    public SimulationVehicleDAO createSimulationVehicleDAO(BigDecimal insuranceSimulationId, Long productId, InsuranceSimulationDTO response, String creationUser, String userAudit) {
        SimulationVehicleDAO simulationVehicleDAO = new SimulationVehicleDAO();
        simulationVehicleDAO.setInsuranceSimulationId(insuranceSimulationId);
        simulationVehicleDAO.setInsuranceProductId(new BigDecimal(productId));
        simulationVehicleDAO.setVehicleBrandId(response.getVehicle().getModel().getBrand().getId());
        simulationVehicleDAO.setVehicleBrandName(response.getVehicle().getModel().getBrand().getName());
        simulationVehicleDAO.setVehicleModelId(response.getVehicle().getModel().getId());
        simulationVehicleDAO.setVehicleModelName(response.getVehicle().getModel().getName());
        simulationVehicleDAO.setVehicleYearId(String.valueOf(response.getVehicle().getModel().getYear()));
        simulationVehicleDAO.setCommercialVehicleAmount(BigDecimal.valueOf(response.getVehicle().getPrice().getAmount()));
        simulationVehicleDAO.setCurrencyId(response.getVehicle().getPrice().getCurrency());
        simulationVehicleDAO.setVehicleCirculationScopeType(response.getVehicleCirculation().getId());
        simulationVehicleDAO.setVehicleGasConversionType(response.getVehicle().getHasPowerSupplyConvertion());
        simulationVehicleDAO.setVehicleLicenseId(response.getVehicle().getLicensePlate());
        simulationVehicleDAO.setCreationUser(creationUser);
        simulationVehicleDAO.setUserAudit(userAudit);
        return simulationVehicleDAO;
    }

    public Map<String, Object> createArgumentsForSaveSimulationVehicle(SimulationVehicleDAO simulationVehicleDAO) {
        Map<String, Object> arguments = new HashMap<>();
        arguments.put(PISDProperties.FIELD_INSURANCE_SIMULATION_ID.getValue(), simulationVehicleDAO.getInsuranceSimulationId());
        arguments.put(PISDProperties.FIELD_OR_FILTER_INSURANCE_PRODUCT_ID.getValue(), simulationVehicleDAO.getInsuranceProductId());
        arguments.put(PISDProperties.FIELD_VEHICLE_BRAND_ID.getValue(), simulationVehicleDAO.getVehicleBrandId());
        arguments.put(PISDProperties.FIELD_VEHICLE_BRAND_NAME.getValue(), simulationVehicleDAO.getVehicleBrandName());
        arguments.put(PISDProperties.FIELD_VEHICLE_MODEL_ID.getValue(), simulationVehicleDAO.getVehicleModelId());
        arguments.put(PISDProperties.FIELD_VEHICLE_MODEL_NAME.getValue(), simulationVehicleDAO.getVehicleModelName());
        arguments.put(PISDProperties.FIELD_VEHICLE_YEAR_ID.getValue(), simulationVehicleDAO.getVehicleYearId());
        arguments.put(PISDProperties.FIELD_COMMERCIAL_VEHICLE_AMOUNT.getValue(), simulationVehicleDAO.getCommercialVehicleAmount());
        arguments.put(PISDProperties.FIELD_CURRENCY_ID.getValue(), simulationVehicleDAO.getCurrencyId());
        arguments.put(PISDProperties.FIELD_NEW_VEHICLE_IND_TYPE.getValue(), simulationVehicleDAO.getNewVehicleIndType());
        arguments.put(PISDProperties.FIELD_VEHICLE_CIRCULATION_SCOPE_TYPE.getValue(), simulationVehicleDAO.getVehicleCirculationScopeType());
        arguments.put(PISDProperties.FIELD_VEH_CHANGE_RUDDER_IND_TYPE.getValue(), simulationVehicleDAO.getVehChangeRudderIndType());
        arguments.put(PISDProperties.FIELD_VEHICLE_GAS_CONVERSION_TYPE.getValue(), simulationVehicleDAO.getVehicleGasConversionType());
        arguments.put(PISDProperties.FIELD_VEHICLE_USE_TYPE.getValue(), simulationVehicleDAO.getVehicleUseType());
        arguments.put(PISDProperties.FIELD_VEHICLE_USAGE_DESC.getValue(), simulationVehicleDAO.getVehicleUsageDesc());
        arguments.put(PISDProperties.FIELD_VEHICLE_LICENSE_ID.getValue(), simulationVehicleDAO.getVehicleLicenseId());
        arguments.put(PISDProperties.FIELD_CREATION_USER_ID.getValue(), simulationVehicleDAO.getCreationUser());
        arguments.put(PISDProperties.FIELD_USER_AUDIT_ID.getValue(), simulationVehicleDAO.getUserAudit());
        return arguments;
    }

    private void setModelAndBrandNames(DatoParticularBO datoParticularBO, VehicleModelDTO model) {
        String etiqueta;
        etiqueta = datoParticularBO.getEtiqueta();
        switch (etiqueta) {
            case MARCA_VEHICULOS:
                model.getBrand().setName(datoParticularBO.getValor());
                break;
            case MODELO_VEHICULOS:
                model.setName(datoParticularBO.getValor());
                break;
            default:
                break;
        }
    }

    public void setApplicationConfigurationService(ApplicationConfigurationService applicationConfigurationService) {
        this.applicationConfigurationService = applicationConfigurationService;
    }
}
