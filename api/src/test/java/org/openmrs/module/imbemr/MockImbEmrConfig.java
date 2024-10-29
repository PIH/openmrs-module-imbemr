package org.openmrs.module.imbemr;

import org.openmrs.PatientIdentifierType;
import org.openmrs.PersonAttributeType;
import org.openmrs.api.PatientService;
import org.openmrs.api.PersonService;

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
