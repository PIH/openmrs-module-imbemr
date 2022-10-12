/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 * <p>
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 * <p>
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.imbemr;

import org.openmrs.Encounter;
import org.openmrs.EncounterProvider;
import org.openmrs.Obs;
import org.openmrs.Order;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientProgram;
import org.openmrs.PatientState;
import org.openmrs.PersonAddress;
import org.openmrs.PersonAttribute;
import org.openmrs.PersonName;
import org.openmrs.Relationship;
import org.openmrs.Visit;
import org.openmrs.annotation.Authorized;
import org.openmrs.api.context.Context;
import org.openmrs.api.db.hibernate.ImmutableOrderInterceptor;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.util.PrivilegeConstants;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Service methods
 */
@Transactional
public class ImbEmrServiceImpl extends BaseOpenmrsService implements ImbEmrService  {

	@Transactional
	@Authorized(PrivilegeConstants.EDIT_PATIENTS)
	public List<String> triggerSyncForPatient(Patient patient) {
		List<String> log = new ArrayList<String>();
		Date now = new Date();

		ImmutableOrderInterceptor orderInterceptor = Context.getRegisteredComponents(ImmutableOrderInterceptor.class).get(0);
		try {
			orderInterceptor.addMutablePropertiesForThread("dateCreated");
			// Update patient
			patient.setDateChanged(now);

			// Update patient names
			for (PersonName name : patient.getNames()) {
				name.setDateChanged(now);
			}

			// Update patient addresses
			for (PersonAddress address : patient.getAddresses()) {
				address.setDateChanged(now);
			}

			// Update patient attributes
			for (PersonAttribute attribute : patient.getAttributes()) {
				attribute.setDateChanged(now);
			}

			// Update patient identifiers
			for (PatientIdentifier identifier : patient.getIdentifiers()) {
				identifier.setDateChanged(now);
			}

			Context.getPatientService().savePatient(patient);
			log.add("Saved Patient: " + patient);

			for (Relationship relationship : Context.getPersonService().getRelationshipsByPerson(patient)) {
				relationship.setDateChanged(now);
				Context.getPersonService().saveRelationship(relationship);
				log.add("Saved Relationship: " + relationship);
			}

			for (Encounter encounter : Context.getEncounterService().getEncountersByPatient(patient)) {
				encounter.setDateChanged(now);
				if (encounter.getVisit() != null) {
					encounter.getVisit().setDateChanged(now);
				}
				for (Order order : encounter.getOrders()) {
					order.setDateCreated(oneSecondLater(order.getDateCreated()));
				}
				for (Obs obs : encounter.getObs()) {
					obs.setDateChanged(now);
				}
				for (EncounterProvider encounterProvider : encounter.getEncounterProviders()) {
					encounterProvider.setDateChanged(now);
					if (encounterProvider.getEncounterRole() != null) {
						encounterProvider.getEncounterRole().setDateChanged(now);
					}
				}
				Context.getEncounterService().saveEncounter(encounter);
				log.add("Saved Encounter: " + encounter);
			}

			for (Visit visit : Context.getVisitService().getVisitsByPatient(patient)) {
				visit.setDateChanged(now);
				Context.getVisitService().saveVisit(visit);
				log.add("Saved Visit: " + visit);
			}

			for (Obs obs : Context.getObsService().getObservationsByPerson(patient)) {
				if (obs.getEncounter() == null) {
					obs.setDateCreated(oneSecondLater(obs.getDateCreated()));
					Context.getObsService().saveObs(obs, "Resaving patient and all data for sync");
					log.add("Saved Obs: " + obs);
				}
			}

			for (PatientProgram patientProgram : Context.getProgramWorkflowService().getPatientPrograms(patient, null, null, null, null, null, true)) {
				patientProgram.setDateChanged(now);
				for (PatientState patientState : patientProgram.getStates()) {
					patientState.setDateChanged(now);
				}
				Context.getProgramWorkflowService().savePatientProgram(patientProgram);
				log.add("Saved Patient Program: " + patientProgram);
			}
		}
		catch (Exception e) {
			log.add("Error: " + e.getMessage());
			throw new RuntimeException(e);
		}
		finally {
			orderInterceptor.removeMutablePropertiesForThread();
		}

		return log;
	}

	private Date oneSecondLater(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.SECOND, 1);
		return cal.getTime();
	}
}
