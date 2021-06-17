package com.bbva.pisd;

import com.bbva.elara.domain.transaction.Context;
import com.bbva.elara.domain.transaction.Severity;
import com.bbva.elara.domain.transaction.TransactionParameter;
import com.bbva.elara.domain.transaction.request.TransactionRequest;
import com.bbva.elara.domain.transaction.request.body.CommonRequestBody;
import com.bbva.elara.domain.transaction.request.header.CommonRequestHeader;
import com.bbva.elara.test.osgi.DummyBundleContext;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.annotation.Resource;

import com.bbva.pisd.dto.insurance.commons.InstallmentsDTO;
import com.bbva.pisd.dto.insurance.financing.EntityOutFinancingPlanDTO;
import com.bbva.pisd.dto.insurance.financing.FinancingPlanDTO;
import com.bbva.pisd.dto.insurance.mock.MockDTO;
import com.bbva.pisd.dto.insurance.simulation.InsuranceSimulationDTO;
import com.bbva.pisd.lib.r030.PISDR030;
import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

/**
 * Test for transaction PISDT00701PETransaction
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
		"classpath:/META-INF/spring/elara-test.xml",
		"classpath:/META-INF/spring/PISDT00701PETest.xml" })
public class PISDT00701PETransactionTest {
	private static final Logger LOGGER = LoggerFactory.getLogger(PISDT00701PETransactionTest.class);

	@Spy
	@Autowired
	private PISDT00701PETransaction transaction;

	@Resource(name = "pisdR030")
	private PISDR030 pisdr030;

	@Resource(name = "dummyBundleContext")
	private DummyBundleContext bundleContext;

	@Mock
	private CommonRequestHeader header;

	@Mock
	private TransactionRequest transactionRequest;

	@Mock
	private List<InstallmentsDTO> installmentsDTOS;

	private MockDTO mockDTO;


	@Before
	public void initializeClass() throws Exception {
		// Initializing mocks
		MockitoAnnotations.initMocks(this);
		// Start BundleContext
		this.transaction.start(bundleContext);
		// Setting Context
		this.transaction.setContext(new Context());
		// Set Body
		CommonRequestBody commonRequestBody = new CommonRequestBody();
		commonRequestBody.setTransactionParameters(new ArrayList<>());
		this.transactionRequest.setBody(commonRequestBody);
		// Set Header Mock
		this.transactionRequest.setHeader(header);
		// Set TransactionRequest
		this.transaction.getContext().setTransactionRequest(transactionRequest);

		mockDTO = MockDTO.getInstance();

		doReturn("1a02cbcc-7298-4db8-895b-e5f6692bc89a").when(this.transaction).getQuotationid();
		doReturn(new Date()).when(this.transaction).getStartdate();
		doReturn(installmentsDTOS).when(this.transaction).getInstallmentplans();
	}

	@Test
	public void testNotNull(){
		// Example to Mock the Header
		// Mockito.doReturn("ES").when(header).getHeaderParameter(RequestHeaderParamsName.COUNTRYCODE);
		Assert.assertNotNull(this.transaction);
		Assert.assertNotNull(this.transaction.getStartdate());
		Assert.assertNotNull(this.transaction.getQuotationid());
		Assert.assertNotNull(this.transaction.getInstallmentplans());
		this.transaction.execute();
	}

	@Test
	public void execute() throws IOException {
		FinancingPlanDTO output = mockDTO.getSimulateInsuranceQuotationInstallmentPlanResponse();
		when(pisdr030.executeSimulateInsuranceQuotationInstallmentPlan(anyObject())).thenReturn(output);
		this.transaction.execute();

		assertTrue(this.transaction.getAdviceList().isEmpty());
	}

	@Test
	public void testNull() {
		when(pisdr030.executeSimulateInsuranceQuotationInstallmentPlan(anyObject())).thenReturn(null);
		this.transaction.execute();
		assertEquals(Severity.ENR.getValue(), this.transaction.getSeverity().getValue());
	}

	// Add Parameter to Transaction
	private void addParameter(final String parameter, final Object value) {
		final TransactionParameter tParameter = new TransactionParameter(parameter, value);
		transaction.getContext().getParameterList().put(parameter, tParameter);
	}

	// Get Parameter from Transaction
	private Object getParameter(final String parameter) {
		final TransactionParameter param = transaction.getContext().getParameterList().get(parameter);
		return param != null ? param.getValue() : null;
	}
}