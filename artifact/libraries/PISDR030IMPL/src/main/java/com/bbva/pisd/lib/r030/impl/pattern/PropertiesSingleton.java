package com.bbva.pisd.lib.r030.impl.pattern;

import com.bbva.elara.configuration.manager.application.ApplicationConfigurationService;

public class PropertiesSingleton {

    protected static ApplicationConfigurationService applicationConfigurationService;

    private PropertiesSingleton(){}

    public static void setApplicationConfigurationService(ApplicationConfigurationService applicationConfigurationService) {
        PropertiesSingleton.applicationConfigurationService = applicationConfigurationService;
    }

    public static String getValue(String key) {
        return PropertiesSingleton.applicationConfigurationService.getProperty(key);
    }

}
