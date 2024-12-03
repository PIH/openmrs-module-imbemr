package org.openmrs.module.imbemr.event;

import org.apache.commons.lang.StringUtils;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.imbemr.ImbEmrConfig;
import org.openmrs.module.mohbilling.model.Beneficiary;
import org.openmrs.module.mohbilling.model.Insurance;
import org.openmrs.module.mohbilling.model.InsurancePolicy;
import org.openmrs.module.mohbilling.service.BillingService;
import org.openmrs.util.ConfigUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jms.MapMessage;
import java.util.ArrayList;
import java.util.List;

/**
 * Listener that can be registered with Patient creation (or update) events and which will create a
 * default private insurance for that patient if they do not already have one
 */
@Component
public class CreateInsurancePatientListener extends PatientEventListener {

	private final PatientService patientService;
	private final ImbEmrConfig imbEmrConfig;

	public CreateInsurancePatientListener(@Autowired PatientService patientService,
										  @Autowired ImbEmrConfig imbEmrConfig) {
		this.patientService = patientService;
		this.imbEmrConfig = imbEmrConfig;
	}

	@Override
	public void handlePatient(String patientUuid, MapMessage mapMessage) {
		// This functionality is enabled or disabled by setting or unsetting this global property value
		String insuranceName = ConfigUtil.getProperty("imbemr.autoCreateInsuranceType");
		if (StringUtils.isNotBlank(insuranceName)) {

			Patient patient = patientService.getPatientByUuid(patientUuid);
			if (patient == null) {
				throw new IllegalArgumentException("unable to retrieve patient with uuid: " + patientUuid);
			}

			Insurance insurance = null;
			for (Insurance i : getBillingService().getAllInsurances()) {
				if (i.getName().equalsIgnoreCase(insuranceName)) {
					insurance = i;
				}
			}
			if (insurance == null) {
				throw new IllegalStateException("Could not find insurance with name " + insuranceName);
			}

			PatientIdentifierType identifierType = imbEmrConfig.getPrimaryCareIdentifierType();
			if (identifierType == null) {
				throw new IllegalStateException("Could not find primary care identifier type");
			}
			List<String> primaryCareIdentifiers = new ArrayList<>();
			for (PatientIdentifier pi : patient.getPatientIdentifiers(identifierType)) {
				primaryCareIdentifiers.add(pi.getIdentifier());
			}

			if (primaryCareIdentifiers.isEmpty()) {
				throw new IllegalStateException("Could not find a primary care identifier for patient " + patient.getUuid());
			}

			List<InsurancePolicy> policies = getBillingService().getAllInsurancePoliciesByPatient(patient);
			boolean foundExistingPolicy = false;
			for (InsurancePolicy policy : policies) {
				if (insurance.equals(policy.getInsurance()) && primaryCareIdentifiers.contains(policy.getInsuranceCardNo())) {
					foundExistingPolicy = true;
				}
			}
			if (!foundExistingPolicy) {
				InsurancePolicy policy = new InsurancePolicy();
				policy.setOwner(patient);
				policy.setInsurance(insurance);
				policy.setInsuranceCardNo(primaryCareIdentifiers.get(0));
				policy.setCoverageStartDate(patient.getDateCreated());
				policy.setCreatedDate(patient.getDateCreated());
				policy.setCreator(patient.getCreator());
				policy.setRetired(false);

				Beneficiary beneficiary =new Beneficiary();
				beneficiary.setPatient(policy.getOwner());
				beneficiary.setInsurancePolicy(policy);
				beneficiary.setCreatedDate(policy.getCreatedDate());
				beneficiary.setCreator(policy.getCreator());
				beneficiary.setRetired(policy.isRetired());
				beneficiary.setPolicyIdNumber(policy.getInsuranceCardNo());
				policy.addBeneficiary(beneficiary);

				getBillingService().saveInsurancePolicy(policy);
				log.debug("Created new insurance policy for patient " + patient.getUuid());
			}
		}
		else {
			if (log.isTraceEnabled()) {
				log.trace("CreateInsurancePatientListener is not enabled, as GP imbemr.autoCreateInsuranceType is not set");
			}
		}
	}

	@Override
	public void handleException(Exception e) {
		log.warn("Unable to create insurance for patient: " + e.getMessage());
		log.debug(e);
	}

	BillingService getBillingService() {
		return Context.getService(BillingService.class);
	}
}
