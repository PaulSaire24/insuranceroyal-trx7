package com.bbva.pisd;

import com.bbva.elara.transaction.AbstractTransaction;
import com.bbva.pisd.dto.insurance.commons.InstallmentsDTO;
import com.bbva.pisd.dto.insurance.financing.FinancingPlanDTO;
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
	 * Set value for FinancingPlanDTO output parameter data
	 */
	protected void setData(final FinancingPlanDTO field){
		this.addParameter("data", field);
	}
}
