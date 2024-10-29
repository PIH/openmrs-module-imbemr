package org.openmrs.module.imbemr;

import ca.uhn.fhir.context.FhirContext;
import org.hl7.fhir.r4.model.Bundle;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifierType;
import org.openmrs.PersonAttributeType;
import org.openmrs.api.PatientService;
import org.openmrs.api.PersonService;
import org.openmrs.module.imbemr.integration.NidaPatientTranslator;

import java.io.InputStream;
import java.text.SimpleDateFormat;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class MockImbEmrConfig extends ImbEmrConfig {

	public MockImbEmrConfig() {
		this(null, null);
	}

	public MockImbEmrConfig(PatientService patientService, PersonService personService) {
		super(patientService, personService);
	}

	@Override
	public PatientIdentifierType getNationalId() {
		PatientIdentifierType t = new PatientIdentifierType();
		t.setUuid(ImbEmrConstants.NATIONAL_ID_UUID);
		t.setName("National ID");
		return t;
	}

	@Override
	public PersonAttributeType getTelephoneNumber() {
		PersonAttributeType t = new PersonAttributeType();
		t.setUuid(ImbEmrConstants.TELEPHONE_NUMBER_UUID);
		t.setName("Phone Number");
		return t;
	}

	@Override
	public PatientIdentifierType getPatientIdentifierTypeByUuid(String uuid) {
		PatientIdentifierType t = new PatientIdentifierType();
		t.setUuid(uuid);
		return t;
	}
}
