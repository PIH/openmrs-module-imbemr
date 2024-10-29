/*
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */

package org.openmrs.module.imbemr.fragment.controller.field;

import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PersonAddress;
import org.openmrs.PersonAttribute;
import org.openmrs.module.imbemr.ImbEmrConstants;
import org.openmrs.module.imbemr.integration.NidaMpiProvider;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.fragment.FragmentModel;
import org.openmrs.ui.framework.fragment.action.FailureResult;
import org.openmrs.ui.framework.fragment.action.FragmentActionResult;
import org.openmrs.ui.framework.fragment.action.ObjectResult;
import org.springframework.web.bind.annotation.RequestParam;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.Map;

public class SearchClientRegistryFragmentController {

    public void controller(@SpringBean NidaMpiProvider mpiProvider, FragmentModel model) {
        model.addAttribute("clientRegistryEnabled", mpiProvider.isEnabled());
    }

    public FragmentActionResult findByIdentifier(@RequestParam("identifier") String identifier,
                                                 @RequestParam("identifierTypeUuid") String identifierType,
                                                 @SpringBean NidaMpiProvider mpiProvider) {
        Patient patient = mpiProvider.fetchMpiPatient(identifier, identifierType);
        if (patient == null) {
            return new FailureResult("imbemr.clientRegistry.patientNotFound");
        }
        Map<String, Object> data = new LinkedHashMap<>();

        data.put("birthdate", patient.getBirthdate() == null ? null : new SimpleDateFormat("yyyy-MM-dd").format(patient.getBirthdate()));
        data.put("givenName", patient.getGivenName());
        data.put("familyName", patient.getFamilyName());
        data.put("gender", patient.getGender());
        if (patient.getBirthdate() != null) {
            Calendar c = Calendar.getInstance();
            c.setTime(patient.getBirthdate());
            data.put("birthdateDay", c.get(Calendar.DAY_OF_MONTH));
            data.put("birthdateMonth", c.get(Calendar.MONTH) + 1);
            data.put("birthdateYear", c.get(Calendar.YEAR));
        }

        // TODO: Ideally retrieve and configure form fields based on registration config.  Hard-code for now.

        for (PatientIdentifier pi : patient.getIdentifiers()) {
            if (pi.getIdentifierType().getUuid().equals(ImbEmrConstants.NATIONAL_ID_UUID)) {
                data.put("nationalId", pi.getIdentifier());
            }
            else if (pi.getIdentifierType().getUuid().equals(ImbEmrConstants.NID_APPLICATION_NUMBER_UUID)) {
                data.put("applicationNumber", pi.getIdentifier());
            }
            else if (pi.getIdentifierType().getUuid().equals(ImbEmrConstants.UPID_UUID)) {
                data.put("upid", pi.getIdentifier());
            }
            else if (pi.getIdentifierType().getUuid().equals(ImbEmrConstants.NIN_UUID)) {
                data.put("nin", pi.getIdentifier());
            }
            else if (pi.getIdentifierType().getUuid().equals(ImbEmrConstants.PASSPORT_NUMBER_UUID)) {
                data.put("passportNumber", pi.getIdentifier());
            }
        }

        for (PersonAttribute pa : patient.getAttributes()) {
            if (pa.getAttributeType().getUuid().equals(ImbEmrConstants.TELEPHONE_NUMBER_UUID)) {
                data.put("phoneNumber", pa.getValue());
            }
            // TODO mothersName, fathersName, educationLevel, profession, religion
        }

        PersonAddress pa = patient.getPersonAddress();
        if (pa != null) {
            data.put("country", pa.getCountry());
            data.put("stateProvince", pa.getStateProvince());
            data.put("countyDistrict", pa.getCountyDistrict());
            data.put("cityVillage", pa.getStateProvince());
            data.put("address3", pa.getAddress3());
            data.put("address1", pa.getAddress1());
        }

        return new ObjectResult(data);
    }

}
