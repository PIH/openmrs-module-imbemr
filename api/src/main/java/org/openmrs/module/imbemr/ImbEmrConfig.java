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

	public PatientIdentifierType getNationalId() {
		return getPatientIdentifierType(ImbEmrConstants.NATIONAL_ID_UUID, ImbEmrConstants.NATIONAL_ID_NAME);
	}

	public PersonAttributeType getTelephoneNumber() {
		return getPersonAttributeType(ImbEmrConstants.TELEPHONE_NUMBER_UUID, ImbEmrConstants.TELEPHONE_NUMBER_NAME);
	}

	// Provides a means to look up a patient identifier type first by the expected uuid, or by name if uuid not found
	private PatientIdentifierType getPatientIdentifierType(String uuid, String name) {
		PatientIdentifierType ret = patientService.getPatientIdentifierTypeByUuid(uuid);
		if (ret == null) {
			ret = patientService.getPatientIdentifierTypeByName(name);
		}
		return ret;
	}

	// Provides a means to look up a person attribute type first by the expected uuid, or by name if uuid not found
	private PersonAttributeType getPersonAttributeType(String uuid, String name) {
		PersonAttributeType ret = personService.getPersonAttributeTypeByUuid(uuid);
		if (ret == null) {
			ret = personService.getPersonAttributeTypeByName(name);
		}
		return ret;
	}
}
