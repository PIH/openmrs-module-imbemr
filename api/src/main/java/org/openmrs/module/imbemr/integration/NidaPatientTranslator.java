/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.imbemr.integration;

import ca.uhn.fhir.model.api.TemporalPrecisionEnum;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hl7.fhir.r4.model.Address;
import org.hl7.fhir.r4.model.ContactPoint;
import org.hl7.fhir.r4.model.Enumerations;
import org.hl7.fhir.r4.model.HumanName;
import org.hl7.fhir.r4.model.Identifier;
import org.hl7.fhir.r4.model.StringType;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.PersonAddress;
import org.openmrs.PersonAttribute;
import org.openmrs.PersonAttributeType;
import org.openmrs.PersonName;
import org.openmrs.module.imbemr.ImbEmrConfig;
import org.openmrs.module.imbemr.ImbEmrConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementation of MpiProvider that connects to the Rwanda NIDA
 */
@Component
public class NidaPatientTranslator {

	protected Log log = LogFactory.getLog(getClass());

	private final ImbEmrConfig imbEmrConfig;

	public NidaPatientTranslator(
			@Autowired ImbEmrConfig imbEmrConfig
	) {
		this.imbEmrConfig = imbEmrConfig;
	}

	public static final Map<String, String> IDENTIFIER_SYSTEMS = new HashMap<>();
	static {
		IDENTIFIER_SYSTEMS.put("NID", ImbEmrConstants.NATIONAL_ID_UUID);
		IDENTIFIER_SYSTEMS.put("NID_APPLICATION_NUMBER", ImbEmrConstants.NID_APPLICATION_NUMBER_UUID);
		IDENTIFIER_SYSTEMS.put("NIN", ImbEmrConstants.NIN_UUID);
		IDENTIFIER_SYSTEMS.put("UPI", ImbEmrConstants.UPID_UUID);
		IDENTIFIER_SYSTEMS.put("PASSPORT", ImbEmrConstants.PASSPORT_NUMBER_UUID);
	}

	public Patient toOpenmrsType(@Nonnull org.hl7.fhir.r4.model.Patient fhirPatient) {
		Patient p = new Patient();

		if (fhirPatient.hasIdentifier()) {
			for (Identifier identifier : fhirPatient.getIdentifier()) {
				String value = identifier.getValue();
				if (StringUtils.isNotBlank(value)) {
					String system = identifier.getSystem();
					PatientIdentifierType identifierType = null;
					String patientIdentifierTypeUuid = IDENTIFIER_SYSTEMS.get(system);
					if (StringUtils.isNotBlank(patientIdentifierTypeUuid)) {
						identifierType = imbEmrConfig.getPatientIdentifierTypeByUuid(patientIdentifierTypeUuid);
					}
					if (identifierType != null) {
						PatientIdentifier pi = new PatientIdentifier();
						pi.setPatient(p);
						pi.setIdentifierType(identifierType);
						pi.setIdentifier(value);
						p.addIdentifier(pi);
					} else {
						log.debug("Not adding identifier of type: " + system);
					}
				}
			}
		}

		if (fhirPatient.hasName()) {
			for (HumanName humanName : fhirPatient.getName()) {
				List<String> givenNames = new ArrayList<>();
				List<String> familyNames = new ArrayList<>();
				if (humanName.hasFamily()) {
					familyNames.add(humanName.getFamily());
				}
				if (humanName.hasGiven()) {
					for (StringType givenPart : humanName.getGiven()) {
						givenNames.add(givenPart.getValue());
					}
				}
				PersonName name = new PersonName();
				name.setPerson(p);
				name.setGivenName(String.join(" ", givenNames));
				name.setFamilyName(String.join(" ", familyNames));
				if (p.getNames().isEmpty()) {
					name.setPreferred(true);
				}
				p.addName(name);
			}
		}

		if (fhirPatient.hasGender()) {
			if (fhirPatient.getGender() == Enumerations.AdministrativeGender.MALE) {
				p.setGender("M");
			} else if (fhirPatient.getGender() == Enumerations.AdministrativeGender.FEMALE) {
				p.setGender("F");
			} else {
				// TODO: Do we handle UNKNOWN or OTHER cases?  If so, do we do U and O like the fhir2 module?
				log.debug("Not adding gender of type: " + fhirPatient.getGender());
			}
		}

		if (fhirPatient.hasBirthDateElement()) {
			p.setBirthdate(fhirPatient.getBirthDateElement().getValue());
			TemporalPrecisionEnum precision = fhirPatient.getBirthDateElement().getPrecision();
			if (precision != null && precision != TemporalPrecisionEnum.DAY) {
				p.setBirthdateEstimated(true);
			}
		}

		if (fhirPatient.hasDeceasedBooleanType()) {
			p.setDead(fhirPatient.getDeceasedBooleanType().booleanValue());
		}

		if (fhirPatient.hasDeceasedDateTimeType()) {
			p.setDead(true);
			p.setDeathDate(fhirPatient.getDeceasedDateTimeType().getValue());
		}

		for (Address fhirAddress : fhirPatient.getAddress()) {
			PersonAddress personAddress = new PersonAddress();
			personAddress.setPerson(p);
			personAddress.setCountry(fhirAddress.getCountry());
			personAddress.setStateProvince(fhirAddress.getState());
			personAddress.setCountyDistrict(fhirAddress.getDistrict());
			personAddress.setCityVillage(fhirAddress.getCity());
			if (fhirAddress.hasLine()) {
				for (int i = 0; i < fhirAddress.getLine().size(); i++) {
					StringType lineEntry = fhirAddress.getLine().get(i);
					if (i == 0) {
						String[] lineComponents = lineEntry.getValue().split("\\|");
						for (int j = 0; j < lineComponents.length; j++) {
							if (j == 0) {
								if (StringUtils.isNotBlank(lineComponents[j])) {
									personAddress.setAddress3(lineComponents[j]); // Cell, TODO is this right?
								}
							}
							else if (j == 1) {
								if (StringUtils.isNotBlank(lineComponents[j])) {
									personAddress.setAddress1(lineComponents[j]); // Umudugudu, TODO is this right?
								}
							}
							else {
								log.debug("Not adding address line 1, component " + j + " = " + lineComponents[j]);
							}
						}
					} else {
						log.debug("Not adding address line element # " + i + " = " + lineEntry.getValue());
					}
				}
			}

			// TODO: Which to set as preferred?  How to use FhirAddress type
			p.addAddress(personAddress);
		}

		if (fhirPatient.hasTelecom()) {
			for (ContactPoint contactPoint : fhirPatient.getTelecom()) {
				if (contactPoint.hasValue()) {
					// TODO: Ignoring system and use properties, assume these indicate the patient's primary phone
					PersonAttributeType phoneNumber = imbEmrConfig.getTelephoneNumber();
					if (phoneNumber != null) {
						PersonAttribute personAttribute = new PersonAttribute();
						personAttribute.setPerson(p);
						personAttribute.setAttributeType(phoneNumber);
						personAttribute.setValue(contactPoint.getValue());
						p.addAttribute(personAttribute);
					} else {
						log.debug("Not adding phone number as no phone number attribute found");
					}
				}
			}
		}

		// TODO: Not handled yet, need more information on how it is set and specified
		// See extension for nationality that seems to have nationality, education level, profession and religion
		// And see "contact" section, which seems to be used for Father Name, Mother Name, and Spouse Name

		return p;
	}
}
