package org.openmrs.module.imbemr;

import ca.uhn.fhir.context.FhirContext;
import org.hl7.fhir.r4.model.Bundle;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PersonAddress;
import org.openmrs.PersonAttribute;
import org.openmrs.module.imbemr.integration.NidaPatientTranslator;

import java.io.InputStream;
import java.text.SimpleDateFormat;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

public class NidaPatientTranslatorTest {

	FhirContext fhirContext;
	NidaPatientTranslator patientTranslator;
	ImbEmrConfig imbEmrConfig;

	@Before
	public void setUp() {
		fhirContext = FhirContext.forR4Cached();
		imbEmrConfig = new MockImbEmrConfig();
		patientTranslator = new NidaPatientTranslator(imbEmrConfig);
	}
	
	@Test
	public void shouldTranslateFromFhirPatientToOpenmrsPatient() throws Exception {
		try (InputStream is = getClass().getClassLoader().getResourceAsStream("client-registry-patient-220919-7657-5617.json")) {
			Bundle bundle = fhirContext.newJsonParser().parseResource(Bundle.class, is);
			org.hl7.fhir.r4.model.Patient fhirPatient = (org.hl7.fhir.r4.model.Patient) bundle.getEntry().get(0).getResource();
			assertThat(fhirPatient, notNullValue());
			Patient p = patientTranslator.toOpenmrsType(fhirPatient);
			assertThat(p, notNullValue());
			assertThat(p.getGender(), equalTo("M"));
			assertThat(new SimpleDateFormat("yyyy-MM-dd").format(p.getBirthdate()), equalTo("1999-03-20"));
			assertThat(p.getNames().size(), equalTo(1));
			assertThat(p.getGivenName(), equalTo("TestP"));
			assertThat(p.getFamilyName(), equalTo("TestP"));
			assertThat(p.getDead(), equalTo(false));
			assertThat(p.getIdentifiers().size(), equalTo(1));
			PatientIdentifier identifier = p.getIdentifiers().iterator().next();
			assertThat(identifier, notNullValue());
			assertThat(identifier.getIdentifier(), equalTo("220919-7657-5617"));
			assertThat(identifier.getIdentifierType(), notNullValue());
			assertThat(identifier.getIdentifierType().getUuid(), equalTo(ImbEmrConstants.UPID_UUID));
			assertThat(p.getActiveAttributes().size(), equalTo(6));
			PersonAttribute phoneNumber = p.getAttribute(imbEmrConfig.getTelephoneNumber());
			assertThat(phoneNumber, notNullValue());
			assertThat(phoneNumber.getValue(), equalTo("0000000000"));
			PersonAttribute mothersName = p.getAttribute(imbEmrConfig.getMothersName());
			assertThat(mothersName, notNullValue());
			assertThat(mothersName.getValue(), equalTo("Test Mother"));
			PersonAttribute fathersName = p.getAttribute(imbEmrConfig.getFathersName());
			assertThat(fathersName, notNullValue());
			assertThat(fathersName.getValue(), equalTo("Test Father"));
			PersonAttribute educationLevel = p.getAttribute(imbEmrConfig.getEducationLevel());
			assertThat(educationLevel, notNullValue());
			assertThat(educationLevel.getValue(), equalTo("Primary"));
			PersonAttribute profession = p.getAttribute(imbEmrConfig.getProfession());
			assertThat(profession, notNullValue());
			assertThat(profession.getValue(), equalTo("1306"));
			PersonAttribute religion = p.getAttribute(imbEmrConfig.getReligion());
			assertThat(religion, notNullValue());
			assertThat(religion.getValue(), equalTo("some-religion"));
			assertThat(p.getAddresses().size(), equalTo(2));
			for (PersonAddress address : p.getAddresses()) {
				if ("Rwanda".equals(address.getCountry())) {
					assertThat(address.getStateProvince(), equalTo("Kigali Province"));
					assertThat(address.getCountyDistrict(), equalTo("Nyarugenge"));
					assertThat(address.getCityVillage(), equalTo("Gihanga"));
					assertThat(address.getAddress3(), equalTo("Gitega"));
					assertThat(address.getAddress1(), equalTo("Akabahizi"));
				}
				else {
					assertThat(address.getCountry(), equalTo("RWA"));
					assertThat(address.getStateProvince(), nullValue());
					assertThat(address.getCountyDistrict(), nullValue());
					assertThat(address.getCityVillage(), nullValue());
					assertThat(address.getAddress3(), nullValue());
					assertThat(address.getAddress1(), nullValue());
				}
			}
		}

	}
}
