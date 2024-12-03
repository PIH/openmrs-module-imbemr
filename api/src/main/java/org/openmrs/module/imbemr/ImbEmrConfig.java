/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.imbemr;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.PatientIdentifierType;
import org.openmrs.PersonAttributeType;
import org.openmrs.api.PatientService;
import org.openmrs.api.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Config used by the IMB EMR module
 */
@Component
public class ImbEmrConfig {

	protected Log log = LogFactory.getLog(getClass());

	private final PersonService personService;

	private final PatientService patientService;

	public ImbEmrConfig(@Autowired PatientService patientService,
						@Autowired PersonService personService) {
		this.patientService = patientService;
		this.personService = personService;
	}

	public PatientIdentifierType getPrimaryCareIdentifierType() {
		return getPatientIdentifierTypeByUuid(ImbEmrConstants.PRIMARY_CARE_ID_UUID);
	}

	public PatientIdentifierType getNationalId() {
		return getPatientIdentifierTypeByUuid(ImbEmrConstants.NATIONAL_ID_UUID);
	}

	public PersonAttributeType getTelephoneNumber() {
		return getPersonAttributeTypeByUuid(ImbEmrConstants.TELEPHONE_NUMBER_UUID);
	}

	public PersonAttributeType getMothersName() {
		return getPersonAttributeTypeByUuid(ImbEmrConstants.MOTHERS_NAME_UUID);
	}

	public PersonAttributeType getFathersName() {
		return getPersonAttributeTypeByUuid(ImbEmrConstants.FATHERS_NAME_UUID);
	}

	public PersonAttributeType getEducationLevel() {
		return getPersonAttributeTypeByUuid(ImbEmrConstants.EDUCATION_LEVEL_UUID);
	}

	public PersonAttributeType getProfession() {
		return getPersonAttributeTypeByUuid(ImbEmrConstants.PROFESSION_UUID);
	}

	public PersonAttributeType getReligion() {
		return getPersonAttributeTypeByUuid(ImbEmrConstants.RELIGION_UUID);
	}

	public PatientIdentifierType getPatientIdentifierTypeByUuid(String uuid) {
		return patientService.getPatientIdentifierTypeByUuid(uuid);
	}

	public PersonAttributeType getPersonAttributeTypeByUuid(String uuid) {
		return personService.getPersonAttributeTypeByUuid(uuid);
	}
}
