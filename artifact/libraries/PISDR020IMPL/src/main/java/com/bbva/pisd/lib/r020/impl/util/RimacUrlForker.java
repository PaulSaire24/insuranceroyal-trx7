package com.bbva.pisd.lib.r020.impl.util;

import com.bbva.elara.configuration.manager.application.ApplicationConfigurationService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RimacUrlForker {
    private static final Logger LOGGER = LoggerFactory.getLogger(RimacUrlForker.class);

    private static final String URI_FINANCING_PLAN = "api.connector.financingplan.rimac.{idProd}.url";
    private static final String URI_PAYMENT_SCHEDULE = "api.connector.paymentschedule.rimac.{idProd}.url";
    private static final String ID_API_FINANCING_PLAN_RIMAC = "financingplan.rimac";
    private static final String ID_API_PAYMENT_SCHEDULE_RIMAC = "paymentschedule.rimac";

    private ApplicationConfigurationService applicationConfigurationService;

    public String generateUriForSignatureAWSQuoteSchedule(String productId, String quotationId){
        String uri = null;
        String urifromConsole = this.applicationConfigurationService.getProperty(URI_FINANCING_PLAN.replace("{idProd}", productId));
        int helper = urifromConsole.indexOf(".com");
        uri = urifromConsole.substring(helper+4);
        
        if(!productId.equals("830")){
            uri = urifromConsole.substring(helper+4).replace("{idCotizacion}", quotationId);
        }

        LOGGER.info("***** RimacUrlForker - generateUriForSignatureAWS ***** uri: {}", uri);

        return uri;
    }

    public String generatePropertyKeyNameQuoteSchedule(String productId){

        String propertyKey = ID_API_FINANCING_PLAN_RIMAC.concat(".").concat(productId);

        LOGGER.info("***** RimacUrlForker - generatePropertyKeyName ***** propertyKey: {}", propertyKey);
        
        return propertyKey;
    }

    public String generateUriForSignatureAWSPaymentSchedule(String productId, String quotationId){

        String urifromConsole = this.applicationConfigurationService.getProperty(URI_PAYMENT_SCHEDULE.replace("{idProd}", productId));
		int helper = urifromConsole.indexOf(".com");
		String uri = urifromConsole.substring(helper+4).replace("{idCotizacion}", quotationId);

        LOGGER.info("***** RimacUrlForker - generateUriForSignatureAWS ***** uri: {}", uri);

        return uri;
    }

    public String generatePropertyKeyNamePaymentSchedule(String productId){

        String propertyKey = ID_API_PAYMENT_SCHEDULE_RIMAC.concat(".").concat(productId);

        LOGGER.info("***** RimacUrlForker - generatePropertyKeyName ***** propertyKey: {}", propertyKey);
        
        return propertyKey;
    }

    public void setApplicationConfigurationService(ApplicationConfigurationService applicationConfigurationService) {
        this.applicationConfigurationService = applicationConfigurationService;
    }
}
