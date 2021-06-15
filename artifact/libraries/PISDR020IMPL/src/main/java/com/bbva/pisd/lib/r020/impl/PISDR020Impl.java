package com.bbva.pisd.lib.r020.impl;

import com.bbva.pisd.dto.insurance.amazon.SignatureAWS;
import com.bbva.pisd.dto.insurance.aso.quotdetail.QuotDetailDAO;
import com.bbva.pisd.dto.insurance.bo.financing.CronogramaPagoBO;
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
import java.util.HashMap;
import java.util.Map;

/**
 * The  interface PISDR020Impl class...
 */
public class PISDR020Impl extends PISDR020Abstract {

	private static final Logger LOGGER = LoggerFactory.getLogger(PISDR020Impl.class);
	private static final String AUTHORIZATION = "Authorization";
	private static final String JSON_LOG = "JSON BODY TO SEND: {}";
	private static final String ID_COTIZACION = "cotizacion-id";

	@Override
	public FinancingPlanBO executeQuoteSchedule (FinancingPlanBO input, String traceId) {
		LOGGER.info("***** PISDR020Impl - executeFinancingPlan START *****");
		LOGGER.info("***** PISDR020Impl - executeFinancingPlan Param: {} *****", input);

		FinancingPlanBO output = null;

		String requestJson = getRequestJson(input);

		SignatureAWS signatureAWS = this.pisdR014.executeSignatureConstruction(requestJson, HttpMethod.POST,
				PISDProperties.URI_FINANCING_PLAN.getValue(),null, traceId);

		HttpEntity<String> entity = new HttpEntity<>(requestJson, createHttpHeadersAWS(signatureAWS));
		LOGGER.info(JSON_LOG, entity.getBody());

		try {
			output = this.externalApiConnector.postForObject(PISDProperties.ID_API_FINANCING_PLAN_RIMAC.getValue(), entity, FinancingPlanBO.class);
		} catch(RestClientException e) {
			LOGGER.info("***** PISDR020Impl - executeFinancingPlan ***** Exception: {}", e.getMessage());
			this.addAdvice(PISDErrors.ERROR_CONNECTION_SCHEDULE_QUOTE_RIMAC_SERVICE.getAdviceCode());
		}

		LOGGER.info("***** PISDR020Impl - executeFinancingPlan ***** Response: {}", getRequestJson(output));
		LOGGER.info("***** PISDR020Impl - executeFinancingPlan END *****");

		return output;
	}

	@Override
	public CronogramaPagoBO executePaymentSchedule(FinancingPlanBO input, String quotationId, String traceId) {
		LOGGER.info("***** PISDR020Impl - executePaymentSchedule START *****");
		LOGGER.info("***** PISDR020Impl - executePaymentSchedule Param: {} *****", input);

		CronogramaPagoBO output = null;
		String uri = PISDProperties.URI_PAYMENT_SCHEDULE.getValue().replace(ID_COTIZACION, quotationId);
		String requestJson = getRequestJson(input);

		SignatureAWS signatureAWS = this.pisdR014.executeSignatureConstruction(requestJson, HttpMethod.POST,
				uri,null, traceId);

		HttpEntity<String> entity = new HttpEntity<>(requestJson, createHttpHeadersAWS(signatureAWS));
		LOGGER.info(JSON_LOG, entity.getBody());

		Map<String, Object> pathParams = new HashMap<>();
		pathParams.put("idCotizacion", quotationId);

		try {
			output = this.externalApiConnector.postForObject(PISDProperties.ID_API_PAYMENT_SCHEDULE_RIMAC.getValue(), entity, CronogramaPagoBO.class, pathParams);
		} catch(RestClientException e) {
			LOGGER.info("***** PISDR020Impl - executePaymentSchedule ***** Exception: {}", e.getMessage());
			this.addAdvice(PISDErrors.ERROR_CONNECTION_PAYMENT_SCHEDULE_RIMAC_SERVICE.getAdviceCode());
		}

		LOGGER.info("***** PISDR020Impl - executePaymentSchedule ***** Response: {}", getRequestJson(output));
		LOGGER.info("***** PISDR020Impl - executePaymentSchedule END *****");

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
