package com.bbva.pisd.lib.r030.impl;

import com.bbva.elara.configuration.manager.application.ApplicationConfigurationService;
import com.bbva.elara.library.AbstractLibrary;
import com.bbva.pisd.lib.r012.PISDR012;
import com.bbva.pisd.lib.r020.PISDR020;
import com.bbva.pisd.lib.r030.PISDR030;
import com.bbva.pisd.lib.r030.impl.util.MapperHelper;

/**
 * This class automatically defines the libraries and utilities that it will use.
 */
public abstract class PISDR030Abstract extends AbstractLibrary implements PISDR030 {

	protected ApplicationConfigurationService applicationConfigurationService;

	protected PISDR012 pisdR012;

	protected PISDR020 pisdR020;

	protected MapperHelper mapperHelper;


	/**
	* @param applicationConfigurationService the this.applicationConfigurationService to set
	*/
	public void setApplicationConfigurationService(ApplicationConfigurationService applicationConfigurationService) {
		this.applicationConfigurationService = applicationConfigurationService;
	}

	/**
	* @param pisdR012 the this.pisdR012 to set
	*/
	public void setPisdR012(PISDR012 pisdR012) {
		this.pisdR012 = pisdR012;
	}

	/**
	* @param pisdR020 the this.pisdR020 to set
	*/
	public void setPisdR020(PISDR020 pisdR020) {
		this.pisdR020 = pisdR020;
	}

	public void setMapperHelper(MapperHelper mapperHelper) { this.mapperHelper = mapperHelper; }

}