package com.bbva.pisd.lib.r020.impl;

import com.bbva.pisd.dto.insurance.amazon.SignatureAWS;
import com.bbva.pisd.dto.insurance.bo.financing.FinancingPlanBO;
import com.bbva.pisd.dto.insurance.utils.PISDErrors;
import com.bbva.pisd.dto.insurance.utils.PISDProperties;
import com.bbva.pisd.lib.r020.impl.util.JsonHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClientException;

import javax.ws.rs.HttpMethod;
import java.nio.charset.StandardCharsets;

/**
 * The  interface PISDR020Impl class...
 */
public class PISDR020Impl extends PISDR020Abstract {

	private static final Logger LOGGER = LoggerFactory.getLogger(PISDR020Impl.class);
	private static final String AUTHORIZATION = "Authorization";
	private static final String JSON_LOG = "JSON BODY TO SEND: {}";


	@Override
	public FinancingPlanBO executeFinancingPlan (FinancingPlanBO input, String traceId) {
		LOGGER.info("***** PISDR020Impl - executeSimulateInsuranceQuotationInstallmentPlan START *****");
		LOGGER.info("***** PISDR020Impl - executeSimulateInsuranceQuotationInstallmentPlan Param: {} *****", input);

		FinancingPlanBO output = null;

		String requestJson = getRequestJson(input);

		SignatureAWS signatureAWS = this.pisdR014.executeSignatureConstruction(requestJson, HttpMethod.POST,
				PISDProperties.URI_FINANCING_PLAN.getValue(),null, traceId);

		HttpEntity<String> entity = new HttpEntity<>(requestJson, createHttpHeadersAWS(signatureAWS));
		LOGGER.info(JSON_LOG, entity.getBody());

		try {
			output = this.externalApiConnector.postForObject(PISDProperties.ID_API_FINANCING_PLAN_RIMAC.getValue(), entity, FinancingPlanBO.class);
		} catch(RestClientException e) {
			LOGGER.info("***** PISDR020Impl - executeSimulateInsuranceQuotationInstallmentPlan ***** Exception: {}", e.getMessage());
			this.addAdvice(PISDErrors.ERROR_CONNECTION_SCHEDULE_QUOTE_RIMAC_SERVICE.getAdviceCode());
		}

		LOGGER.info("***** PISDR020Impl - executeSimulateInsuranceQuotationInstallmentPlan ***** Response: {}", getRequestJson(output));
		LOGGER.info("***** PISDR020Impl - executeSimulateInsuranceQuotationInstallmentPlan END *****");

		return output;
	}

	private String getRequestJson(Object o) {
		return JsonHelper.getInstance().toJsonString(o);
	}

	private HttpHeaders createHttpHeadersAWS(SignatureAWS signature) {
		HttpHeaders headers = new HttpHeaders();
		MediaType mediaType = new MediaType("application", "json", StandardCharsets.UTF_8);
		headers.setContentType(mediaType);
		headers.set(AUTHORIZATION, signature.getAuthorization());
		headers.set("X-Amz-Date", signature.getxAmzDate());
		headers.set("x-api-key", signature.getxApiKey());
		headers.set("traceId", signature.getTraceId());
		return headers;
	}
}
