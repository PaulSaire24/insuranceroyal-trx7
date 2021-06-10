package com.bbva.pisd;

import com.bbva.elara.domain.transaction.Severity;
import com.bbva.elara.domain.transaction.response.HttpResponseCode;
import com.bbva.pisd.dto.insurance.financing.EntityOutFinancingPlanDTO;
import com.bbva.pisd.dto.insurance.financing.FinancingPlanDTO;
import com.bbva.pisd.lib.r030.PISDR030;
import com.bbva.elara.domain.transaction.RequestHeaderParamsName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Get financing plan
 *
 */
public class PISDT00701PETransaction extends AbstractPISDT00701PETransaction {

	private static final Logger LOGGER = LoggerFactory.getLogger(PISDT00701PETransaction.class);

	/**
	 * The execute method...
	 */
	@Override
	public void execute() {


		LOGGER.info("PISDT00701PETransaction START");

		LOGGER.info("Cabecera traceId: {}", this.getRequestHeader().getHeaderParameter(RequestHeaderParamsName.REQUESTID));

		PISDR030 pisdr030 = this.getServiceLibrary(PISDR030.class);

		FinancingPlanDTO input = new FinancingPlanDTO();
		input.setQuotationId(this.getQuotationid());
		input.setStartDate(this.getStartdate());
		input.setInstallmentPlans(this.getIntallmentplans());
		input.setCreationUser((String) this.getRequestHeader().getHeaderParameter(RequestHeaderParamsName.USERCODE));
		input.setUserAudit((String) this.getRequestHeader().getHeaderParameter(RequestHeaderParamsName.USERCODE));
		input.setSaleChannelId((String) this.getRequestHeader().getHeaderParameter(RequestHeaderParamsName.CHANNELCODE));
		input.setTraceId((String) this.getRequestHeader().getHeaderParameter(RequestHeaderParamsName.REQUESTID));

		EntityOutFinancingPlanDTO output = pisdr030.executeSimulateInsuranceQuotationInstallmentPlan(input);

		if(output != null) {
			this.setData(output);
			this.setHttpResponseCode(HttpResponseCode.HTTP_CODE_200, Severity.OK);
		} else {
			this.setSeverity(Severity.ENR);
		}
	}

}
