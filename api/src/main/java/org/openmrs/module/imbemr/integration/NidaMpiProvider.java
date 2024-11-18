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
import org.openmrs.module.imbemr.ImbEmrConstants;
import org.openmrs.util.ConfigUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * Implementation of MpiPatientFetcher that connects to the Rwandan Client Register
 */
@Component("nidaMpiProvider")
public class NidaMpiProvider {

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

	public boolean isEnabled() {
		String url = ConfigUtil.getProperty(ImbEmrConstants.CLIENT_REGISTRY_URL_PROPERTY);
		String username = ConfigUtil.getProperty(ImbEmrConstants.CLIENT_REGISTRY_USERNAME_PROPERTY);
		String password = ConfigUtil.getProperty(ImbEmrConstants.CLIENT_REGISTRY_PASSWORD_PROPERTY);
		return StringUtils.isNotBlank(url) && StringUtils.isNotBlank(username) && StringUtils.isNotBlank(password);
	}

	/**
	 * Ultimately, we should likely adopt and integrate this solution:
	 * https://github.com/openmrs/openmrs-module-clientregistry
	 */
	public Patient fetchPatient(String patientId, String identifierTypeUuid) {
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
				return patientTranslator.toOpenmrsType(fhirPatient);
			}
		} catch (Exception e) {
			log.debug("An error occurred trying to fetch patients from NIDA, returning null", e);
		}

		return null;
	}
}