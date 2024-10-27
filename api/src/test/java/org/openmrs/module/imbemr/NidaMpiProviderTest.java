package org.openmrs.module.imbemr;

import ca.uhn.fhir.context.FhirContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.module.imbemr.integration.NidaMpiProvider;
import org.openmrs.module.imbemr.integration.NidaPatientTranslator;
import org.openmrs.module.registrationcore.api.mpi.common.MpiPatient;
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
	ImbEmrConfig imbEmrConfig;

	@Before
	public void setUp() {
		fhirContext = FhirContext.forR4Cached();
		imbEmrConfig = new MockImbEmrConfig();
		patientTranslator = new NidaPatientTranslator(imbEmrConfig);
		provider = new NidaMpiProvider(fhirContext, patientTranslator);
	}
	
	@Test
	public void shouldTestUsingHttp() {
		if (!isConfiguredToRun()) {
			log.warn("NOT EXECUTING " + getClass().getSimpleName() + " AS CLIENT REGISTRY CONFIGURATION IS MISSING");
			log.warn("THE REQUIRED PROPERTIES SHOULD BE SET USING `-Dproperty=value` WHEN EXECUTING THE TEST");
			return;
		}
		MpiPatient mpiPatient = provider.fetchMpiPatient("220919-7657-5617", ImbEmrConstants.NATIONAL_ID_UUID);
		assertThat(mpiPatient, notNullValue());
		assertThat(mpiPatient.getGender(), equalTo("M"));
		assertThat(new SimpleDateFormat("yyyy-MM-dd").format(mpiPatient.getBirthdate()), equalTo("1999-03-20"));
	}

	private boolean isConfiguredToRun() {
		return  ConfigUtil.getSystemProperty(ImbEmrConstants.CLIENT_REGISTRY_URL_PROPERTY) != null &&
				ConfigUtil.getSystemProperty(ImbEmrConstants.CLIENT_REGISTRY_USERNAME_PROPERTY) != null &&
				ConfigUtil.getSystemProperty(ImbEmrConstants.CLIENT_REGISTRY_PASSWORD_PROPERTY) != null;
	}
}
