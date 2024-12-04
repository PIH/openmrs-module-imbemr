package org.openmrs.module.imbemr;

import ca.uhn.fhir.context.FhirContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.openmrs.Patient;
import org.openmrs.module.imbemr.integration.NidaMpiProvider;
import org.openmrs.module.imbemr.integration.NidaPatientTranslator;
import org.openmrs.util.ConfigUtil;

import java.text.SimpleDateFormat;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class NidaMpiProviderTest {

	protected Log log = LogFactory.getLog(getClass());

	FhirContext fhirContext;
	NidaPatientTranslator patientTranslator;
	NidaMpiProvider provider;
	LocationTagUtil locationTagUtil;
	ImbEmrConfig imbEmrConfig;

	@Before
	public void setUp() {
		fhirContext = FhirContext.forR4Cached();
		imbEmrConfig = new MockImbEmrConfig();
		locationTagUtil = Mockito.mock(LocationTagUtil.class);
		patientTranslator = new NidaPatientTranslator(imbEmrConfig);
		provider = new NidaMpiProvider(fhirContext, patientTranslator, locationTagUtil, imbEmrConfig);
	}
	
	@Test
	public void shouldTestUsingHttp() {
		if (!isConfiguredToRun()) {
			log.warn("NOT EXECUTING " + getClass().getSimpleName() + " AS CLIENT REGISTRY CONFIGURATION IS MISSING");
			log.warn("THE REQUIRED PROPERTIES SHOULD BE SET USING `-Dproperty=value` WHEN EXECUTING THE TEST");
			return;
		}
		Patient patient = provider.fetchPatientFromClientRegistry("220919-7657-5617");
		assertThat(patient, notNullValue());
		assertThat(patient.getGender(), equalTo("M"));
		assertThat(new SimpleDateFormat("yyyy-MM-dd").format(patient.getBirthdate()), equalTo("1999-03-20"));
	}

	private boolean isConfiguredToRun() {
		return  ConfigUtil.getSystemProperty(ImbEmrConstants.MPI_URL_PROPERTY) != null &&
				ConfigUtil.getSystemProperty(ImbEmrConstants.MPI_USERNAME_PROPERTY) != null &&
				ConfigUtil.getSystemProperty(ImbEmrConstants.MPI_PASSWORD_PROPERTY) != null;
	}
}
