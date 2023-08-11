package com.bbva.pisd.lib.r030.impl.factory;

import com.bbva.elara.configuration.manager.application.ApplicationConfigurationService;
import com.bbva.pisd.lib.r030.impl.util.Constants;

import java.util.Objects;

public class FactoryRequestQuotation {

    private FactoryRequestQuotation(){}

    public static RequestSchedule getRequestRimac(String insuranceBusinessName, String productShortDesc, ApplicationConfigurationService applicationConfigurationService){
        if(Objects.nonNull(insuranceBusinessName) && (
                insuranceBusinessName.equals(Constants.BUSINESS_NAME_VIDA) || insuranceBusinessName.equals(Constants.BUSINESS_NAME_FAKE_EASYYES))){
            return new RequestLife(productShortDesc,applicationConfigurationService);
        }else{
            return new RequestNoLife(applicationConfigurationService);
        }
    }

}
