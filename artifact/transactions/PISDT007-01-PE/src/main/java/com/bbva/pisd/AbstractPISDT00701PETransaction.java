package com.bbva.pisd;

import com.bbva.elara.transaction.AbstractTransaction;
import com.bbva.pisd.dto.insurance.commons.InstallmentsDTO;
import java.util.Date;
import java.util.List;

/**
 * In this class, the input and output data is defined automatically through the setters and getters.
 */
public abstract class AbstractPISDT00701PETransaction extends AbstractTransaction {

	public AbstractPISDT00701PETransaction(){
	}


	/**
	 * Return value for input parameter quotationId
	 */
	protected String getQuotationid(){
		return (String)this.getParameter("quotationId");
	}

	/**
	 * Return value for input parameter startDate
	 */
	protected Date getStartdate(){
		return (Date)this.getParameter("startDate");
	}

	/**
	 * Return value for input parameter intallmentPlans
	 */
	protected List<InstallmentsDTO> getIntallmentplans(){
		return (List<InstallmentsDTO>)this.getParameter("intallmentPlans");
	}

	/**
	 * Set value for Date output parameter startDate
	 */
	protected void setStartdate(final Date field){
		this.addParameter("startDate", field);
	}

	/**
	 * Set value for Date output parameter maturityDate
	 */
	protected void setMaturitydate(final Date field){
		this.addParameter("maturityDate", field);
	}

	/**
	 * Set value for Long output parameter totalNumberInstallments
	 */
	protected void setTotalnumberinstallments(final Long field){
		this.addParameter("totalNumberInstallments", field);
	}

	/**
	 * Set value for List<InstallmentsDTO> output parameter installmentPlans
	 */
	protected void setInstallmentplans(final List<InstallmentsDTO> field){
		this.addParameter("installmentPlans", field);
	}
}
