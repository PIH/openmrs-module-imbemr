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

import ca.uhn.fhir.context.FhirContext;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.hl7.fhir.r4.model.Bundle;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.module.imbemr.ImbEmrConstants;
import org.openmrs.module.registrationcore.api.mpi.common.MpiPatient;
import org.openmrs.module.registrationcore.api.mpi.common.MpiProvider;
import org.openmrs.module.registrationcore.api.search.PatientAndMatchQuality;
import org.openmrs.util.ConfigUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Implementation of MpiProvider that connects to the Rwanda NIDA
 */
@Component("nidaMpiProvider")
public class NidaMpiProvider implements MpiProvider<Patient> {

	protected Log log = LogFactory.getLog(getClass());

	public static final List<String> SUPPORTED_IDENTIFIER_TYPES = Arrays.asList(
			ImbEmrConstants.NATIONAL_ID_UUID,
			ImbEmrConstants.NID_APPLICATION_NUMBER_UUID,
			ImbEmrConstants.NIN_UUID,
			ImbEmrConstants.UPID_UUID,
			ImbEmrConstants.PASSPORT_NUMBER_UUID
	);

	private final FhirContext fhirContext;
	private final NidaPatientTranslator patientTranslator;

	public NidaMpiProvider(
			@Autowired @Qualifier("fhirR4") FhirContext fhirContext,
			@Autowired NidaPatientTranslator nidaPatientTranslator
	) {
		this.fhirContext = fhirContext;
		this.patientTranslator = nidaPatientTranslator;
	}

	/**
	 * Ultimately, we should likely adopt and integrate this solution:
	 * https://github.com/openmrs/openmrs-module-clientregistry
	 */
	@Override
	public MpiPatient fetchMpiPatient(String patientId, String identifierTypeUuid) {
		if (!SUPPORTED_IDENTIFIER_TYPES.contains(identifierTypeUuid)) {
			return null;
		}
		String url = ConfigUtil.getProperty(ImbEmrConstants.CLIENT_REGISTRY_URL_PROPERTY);
		String username = ConfigUtil.getProperty(ImbEmrConstants.CLIENT_REGISTRY_USERNAME_PROPERTY);
		String password = ConfigUtil.getProperty(ImbEmrConstants.CLIENT_REGISTRY_PASSWORD_PROPERTY);
		if (StringUtils.isBlank(url) || StringUtils.isBlank(username) || StringUtils.isBlank(password)) {
			log.debug("Incomplete credentials supplied to fetch patient from NIDA, skipping");
			return null;
		}

		try (CloseableHttpClient httpClient = HttpUtils.getHttpClient(username, password, true)) {
			HttpGet httpGet = new HttpGet(url + "/Patient?identifier=" + patientId);
			log.debug("Attempting to find patient " + patientId + " from NIDA");
			try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
				int statusCode = response.getStatusLine().getStatusCode();
				HttpEntity entity = response.getEntity();
				String data = "";
				try {
					data = EntityUtils.toString(entity);
				} catch (Exception ignored) {
				}
				if (statusCode != 200) {
					throw new IllegalStateException("Http Status Code: " + statusCode + "; Response: " + data);
				}
				Bundle bundle = fhirContext.newJsonParser().parseResource(Bundle.class, data);
				if (bundle == null || bundle.getEntry() == null || bundle.getEntry().size() != 1) {
					throw new IllegalStateException("Unexpected bundle found: " + bundle);
				}
				org.hl7.fhir.r4.model.Patient fhirPatient = (org.hl7.fhir.r4.model.Patient) bundle.getEntry().get(0).getResource();
				Patient openmrsPatient = patientTranslator.toOpenmrsType(fhirPatient);
				return new MpiPatient(openmrsPatient);
			}
		} catch (Exception e) {
			log.debug("An error occurred trying to fetch patients from NIDA, returning null", e);
		}

		return null;
	}

	@Override
	public MpiPatient fetchMpiPatient(PatientIdentifier patientIdentifier) {
		return fetchMpiPatient(patientIdentifier.getIdentifier(), patientIdentifier.getIdentifierType().getUuid());
	}

	@Override
	public List<PatientAndMatchQuality> findExactMatches(Patient patient, Map<String, Object> otherDataPoints, Double cutoff, Integer maxResults) {
		// For now, just iterate over every provided identifier to search on, and try to fetch a patient from the mpi that matches
		List<PatientAndMatchQuality> results = new ArrayList<>();
		for (PatientIdentifier pi : patient.getIdentifiers()) {
			MpiPatient match = fetchMpiPatient(pi);
			if (match != null) {
				results.add(new PatientAndMatchQuality(match, cutoff, Collections.singletonList("identifier." + pi.getIdentifierType().getUuid())));
			}
		}
		return results;
	}

	@Override
	public List<PatientAndMatchQuality> findSimilarMatches(Patient patient, Map<String, Object> otherDataPoints, Double cutoff, Integer maxResults) {
		// We can expand this in the future if we want to query for possible matches in NIDA.  For now, we just support exact matching by ID
		return findExactMatches(patient, otherDataPoints, cutoff, maxResults);
	}

	@Override
	public String exportPatient(Patient patient) {
		throw new UnsupportedOperationException("Saving patient records in NIDA is not yet supported");
	}

	@Override
	public void updatePatient(Patient patient) {
		throw new UnsupportedOperationException("Updating patient records in NIDA is not yet supported");
	}
}
