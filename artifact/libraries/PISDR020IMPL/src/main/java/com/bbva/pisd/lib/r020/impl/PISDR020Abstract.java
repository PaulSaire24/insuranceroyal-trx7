package com.bbva.pisd.lib.r020.impl;

import com.bbva.elara.configuration.manager.application.ApplicationConfigurationService;
import com.bbva.elara.library.AbstractLibrary;
import com.bbva.elara.utility.api.connector.APIConnector;
import com.bbva.elara.utility.api.connector.APIConnectorBuilder;
import com.bbva.pisd.lib.r014.PISDR014;
import com.bbva.pisd.lib.r020.PISDR020;
import com.bbva.pisd.lib.r020.impl.util.RimacUrlForker;

/**
 * This class automatically defines the libraries and utilities that it will use.
 */
public abstract class PISDR020Abstract extends AbstractLibrary implements PISDR020 {

	protected ApplicationConfigurationService applicationConfigurationService;

	protected APIConnector externalApiConnector;

	protected APIConnectorBuilder apiConnectorBuilder;

	protected PISDR014 pisdR014;

	protected RimacUrlForker rimacUrlForker;
	/**
	* @param applicationConfigurationService the this.applicationConfigurationService to set
	*/
	public void setApplicationConfigurationService(ApplicationConfigurationService applicationConfigurationService) {
		this.applicationConfigurationService = applicationConfigurationService;
	}

	/**
	* @param externalApiConnector the this.externalApiConnector to set
	*/
	public void setExternalApiConnector(APIConnector externalApiConnector) {
		this.externalApiConnector = externalApiConnector;
	}

	/**
	* @param apiConnectorBuilder the this.apiConnectorBuilder to set
	*/
	public void setApiConnectorBuilder(APIConnectorBuilder apiConnectorBuilder) {
		this.apiConnectorBuilder = apiConnectorBuilder;
	}

	/**
	* @param pisdR014 the this.pisdR014 to set
	*/
	public void setPisdR014(PISDR014 pisdR014) {
		this.pisdR014 = pisdR014;
	}

	public void setRimacUrlForker(RimacUrlForker rimacUrlForker) { this.rimacUrlForker = rimacUrlForker; }
}