package org.openmrs.module.imbemr;

import org.openmrs.LocationAttributeType;
import org.openmrs.PatientIdentifierType;
import org.openmrs.PersonAttributeType;
import org.openmrs.api.LocationService;
import org.openmrs.api.PatientService;
import org.openmrs.api.PersonService;

public class MockImbEmrConfig extends ImbEmrConfig {

	public MockImbEmrConfig() {
		this(null, null, null);
	}

	public MockImbEmrConfig(PatientService patientService, PersonService personService, LocationService locationService) {
		super(patientService, personService, locationService);
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
	public PersonAttributeType getMothersName() {
		PersonAttributeType t = new PersonAttributeType();
		t.setUuid(ImbEmrConstants.MOTHERS_NAME_UUID);
		t.setName("Mothers Name");
		return t;
	}

	@Override
	public PersonAttributeType getFathersName() {
		PersonAttributeType t = new PersonAttributeType();
		t.setUuid(ImbEmrConstants.FATHERS_NAME_UUID);
		t.setName("Fathers Name");
		return t;
	}

	@Override
	public PersonAttributeType getEducationLevel() {
		PersonAttributeType t = new PersonAttributeType();
		t.setUuid(ImbEmrConstants.EDUCATION_LEVEL_UUID);
		t.setName("Education Level");
		return t;
	}

	@Override
	public PersonAttributeType getProfession() {
		PersonAttributeType t = new PersonAttributeType();
		t.setUuid(ImbEmrConstants.PROFESSION_UUID);
		t.setName("Profession");
		return t;
	}

	@Override
	public PersonAttributeType getReligion() {
		PersonAttributeType t = new PersonAttributeType();
		t.setUuid(ImbEmrConstants.RELIGION_UUID);
		t.setName("Religion");
		return t;
	}

	@Override
	public LocationAttributeType getFosaId() {
		LocationAttributeType t = new LocationAttributeType();
		t.setUuid(ImbEmrConstants.FOSA_ID_UUID);
		t.setName("FOSA ID");
		return t;
	}

	@Override
	public PersonAttributeType getPersonAttributeTypeByUuid(String uuid) {
		return super.getPersonAttributeTypeByUuid(uuid);
	}

	@Override
	public PatientIdentifierType getPatientIdentifierTypeByUuid(String uuid) {
		PatientIdentifierType t = new PatientIdentifierType();
		t.setUuid(uuid);
		return t;
	}
}
