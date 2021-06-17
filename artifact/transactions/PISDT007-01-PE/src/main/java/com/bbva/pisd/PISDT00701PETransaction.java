package com.bbva.pisd;

import com.bbva.elara.domain.transaction.Severity;
import com.bbva.elara.domain.transaction.response.HttpResponseCode;
import com.bbva.pisd.dto.insurance.financing.FinancingPlanDTO;
import com.bbva.pisd.lib.r030.PISDR030;
import com.bbva.elara.domain.transaction.RequestHeaderParamsName;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

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
		DateTimeZone dateTimeZone = DateTimeZone.forID("Etc/GMT-5");

		FinancingPlanDTO input = new FinancingPlanDTO();
		input.setQuotationId(this.getQuotationid());
		if(Objects.nonNull(this.getStartdate()))
			input.setStartDate(new LocalDate(this.getStartdate(), dateTimeZone));
		input.setInstallmentPlans(this.getInstallmentplans());
		input.setCreationUser((String) this.getRequestHeader().getHeaderParameter(RequestHeaderParamsName.USERCODE));
		input.setUserAudit((String) this.getRequestHeader().getHeaderParameter(RequestHeaderParamsName.USERCODE));
		input.setSaleChannelId((String) this.getRequestHeader().getHeaderParameter(RequestHeaderParamsName.CHANNELCODE));
		input.setTraceId((String) this.getRequestHeader().getHeaderParameter(RequestHeaderParamsName.REQUESTID));

		FinancingPlanDTO output = pisdr030.executeSimulateInsuranceQuotationInstallmentPlan(input);

		if(output != null) {
			this.setStartdate(output.getStartDate().toDateTimeAtCurrentTime().toDate());
			this.setMaturitydate(output.getMaturityDate().toDateTimeAtCurrentTime().toDate());
			this.setInstallmentplans(output.getInstallmentPlans());
			this.setHttpResponseCode(HttpResponseCode.HTTP_CODE_200, Severity.OK);
		} else {
			this.setSeverity(Severity.ENR);
		}
	}

}