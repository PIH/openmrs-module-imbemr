package org.openmrs.module.imbemr.page.controller.patient;

import lombok.Data;
import org.apache.commons.lang.StringUtils;
import org.openmrs.Patient;
import org.openmrs.Relationship;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.messagesource.MessageSourceService;
import org.openmrs.module.emrapi.patient.PatientDomainWrapper;
import org.openmrs.module.mohbilling.businesslogic.InsurancePolicyUtil;
import org.openmrs.module.mohbilling.businesslogic.InsuranceUtil;
import org.openmrs.module.mohbilling.model.InsurancePolicy;
import org.openmrs.module.mohbilling.service.BillingService;
import org.openmrs.ui.framework.annotation.BindParams;
import org.openmrs.ui.framework.annotation.InjectBeans;
import org.openmrs.ui.framework.annotation.MethodParam;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.page.PageModel;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.ObjectError;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class InsurancePolicyPageController {

    public InsurancePolicy getPolicy(@RequestParam(value = "patientId") Patient patient,
                                     @RequestParam(value = "policyId", required = false) Integer policyId) {
        BillingService billingService = Context.getService(BillingService.class);
        InsurancePolicy policy;
        if (policyId != null) {
            policy = billingService.getInsurancePolicy(policyId);
        }
        else {
            policy = new InsurancePolicy();
            policy.setOwner(patient);
        }
        return policy;
    }

    public String get(PageModel model,
                      @MethodParam("getPolicy") @BindParams InsurancePolicy policy,
                      @InjectBeans PatientDomainWrapper patientDomainWrapper,
                      @RequestParam(value = "patientId") Patient patient,
                      @RequestParam(value = "returnUrl", required = false) String returnUrl) throws IOException {

        if (!Context.hasPrivilege("Create Insurance Policy")) {
            return "redirect:/index.htm";
        }
        patientDomainWrapper.setPatient(patient);
        model.addAttribute("patient", patientDomainWrapper);
        model.addAttribute("policy", new InsurancePolicyModel(policy));
        model.addAttribute("insurances", InsuranceUtil.getInsurances(true));
        model.addAttribute("thirdParties", InsurancePolicyUtil.getAllThirdParties());
        model.addAttribute("owners", getEligiblePolicyOwnersForPatient(patient));
        model.addAttribute("returnUrl", getReturnUrl(returnUrl, patient, policy));

        return "patient/insurancePolicy";
    }

    public String post(@InjectBeans PatientDomainWrapper patientDomainWrapper,
                       @MethodParam("getPolicy") InsurancePolicy policy,
                       @BindParams InsurancePolicyModel policyModel,
                       BindingResult errors,
                       @RequestParam(value = "patientId") Patient patient,
                       @RequestParam(value = "returnUrl", required = false) String returnUrl,
                       @SpringBean("messageSourceService") MessageSourceService mss,
                       PageModel model,
                       HttpServletRequest request) {

        patientDomainWrapper.setPatient(patient);

        try {
            if (!Context.hasPrivilege("Create Insurance Policy")) {
                throw new APIException(mss.getMessage("require.unauthorized"));
            }

            InsurancePolicy existingPolicy = Context.getService(BillingService.class).getInsurancePolicyByCardNo(policyModel.getInsuranceCardNo());
            if (existingPolicy != null) {
                if (existingPolicy.getInsurancePolicyId().equals(policy.getInsurancePolicyId())) {
                    // TODO: In the billing module, this leads to an error.  One is not allowed to edit an existing policy.  Is this what we want?
                    // errors.rejectValue("insuranceCardNo", "imbemr.insurance.error.cannotEdit");
                }
                else {
                    // TODO: In the billing module, this also leads to an error.  But can't the same card number exist for different insurance types?
                    errors.rejectValue("insuranceCardNo", "imbemr.insurance.error.duplicateCardNumber");
                }
            }

            policy.setInsurance(InsuranceUtil.getInsurance(policyModel.getInsuranceId()));
            policy.setOwner(policyModel.getOwner());
            policy.setInsuranceCardNo(policyModel.getInsuranceCardNo());
            policy.setCoverageStartDate(policyModel.getCoverageStartDate());
            policy.setExpirationDate(policyModel.getExpirationDate());
            policy.setThirdParty(policyModel.getThirdPartyId() == null ? null : InsurancePolicyUtil.getThirdParty(policyModel.getThirdPartyId()));
            policy.setCreatedDate(new Date());
            policy.setCreator(Context.getAuthenticatedUser());

            InsurancePolicyValidator validator = new InsurancePolicyValidator();
            validator.validate(policy, errors);

            // TODO: Handle retire/unretire (retired, retiredBy, retiredDate, retireReason
            // TODO: Handle Set<Beneficiary>
            // TODO: What about the Set<Admission> property?

            if (errors.hasErrors()) {
                String message = "";
                for (ObjectError error : errors.getAllErrors()) {
                    Object[] arguments = error.getArguments();
                    String errorMessage = mss.getMessage(error.getCode(), arguments, Context.getLocale());
                    if (arguments != null) {
                        for (int i = 0; i < arguments.length; i++) {
                            String argument = (String) arguments[i];
                            errorMessage = errorMessage.replaceAll("\\{" + i + "\\}", argument);
                        }
                    }
                    message = message.concat(errorMessage).concat("<br>");
                }
                throw new APIException(message);
            }

            Context.getService(BillingService.class).saveInsurancePolicy(policy);
        }
        catch (Exception e) {
            request.getSession().setAttribute("emr.errorMessage", e.getMessage());
            model.addAttribute("patient", patientDomainWrapper);
            model.addAttribute("policy", policyModel);
            model.addAttribute("insurances", InsuranceUtil.getInsurances(true));
            model.addAttribute("thirdParties", InsurancePolicyUtil.getAllThirdParties());
            model.addAttribute("owners", getEligiblePolicyOwnersForPatient(patient));
            model.addAttribute("returnUrl", getReturnUrl(returnUrl, patient, policy));
            return "patient/insurancePolicy";
        }

        request.getSession().setAttribute("emr.infoMessage", mss.getMessage("imbemr.insurancePolicy.saved"));
        request.getSession().setAttribute("emr.toastMessage", "true");
        return "redirect:" + getReturnUrl(returnUrl, patient, policy);
    }

    public String getReturnUrl(String returnUrl, Patient patient, InsurancePolicy policy) {
        if (StringUtils.isBlank(returnUrl)) {
            returnUrl = "/imbemr/patient/insurancePolicies.page?patientId=" + patient.getId();
        }
        else if (returnUrl.equalsIgnoreCase("registrationSummary")) {
            returnUrl = "/registrationapp/registrationSummary.page?patientId=" + patient.getId() + "&appId=imbemr.registerPatient";
        }
        returnUrl = returnUrl.replace("{{patientId}}", patient.getId().toString());
        String policyId = (policy == null || policy.getInsurancePolicyId() == null ? "" : policy.getInsurancePolicyId().toString());
        returnUrl = returnUrl.replace("{{policyId}}", policyId);
        return returnUrl;
    }

    // TODO: Limit this to particular relationship types?
    public List<Patient> getEligiblePolicyOwnersForPatient(Patient patient) {
        List<Patient> ret = new ArrayList<>();
        ret.add(patient);
        for (Relationship relationship : Context.getPersonService().getRelationshipsByPerson(patient)) {
            if (relationship.getPersonA() != null) {
                Patient patientA = Context.getPatientService().getPatient(relationship.getPersonA().getPersonId());
                if (patientA != null && !patientA.equals(patient)) {
                    ret.add(patientA);
                }
            }
            if (relationship.getPersonB() != null) {
                Patient patientB = Context.getPatientService().getPatient(relationship.getPersonB().getPersonId());
                if (patientB != null && !patientB.equals(patient)) {
                    ret.add(patientB);
                }
            }
        }
        return ret;
    }

    public static class InsurancePolicyValidator implements Validator {
        @Override
        public boolean supports(Class<?> clazz) {
            return InsurancePolicy.class.isAssignableFrom(clazz);
        }

        @Override
        public void validate(Object o, Errors errors) {
            InsurancePolicy policy = (InsurancePolicy) o;
            if (policy.getInsurance() == null) {
                errors.rejectValue("insurance", "required");
            }
            if (policy.getOwner() == null) {
                errors.rejectValue("owner", "required");
            }
            if (StringUtils.isBlank((policy.getInsuranceCardNo()))) {
                errors.rejectValue("insuranceCardNo", "required");
            }
            if (policy.getCoverageStartDate() == null) {
                errors.rejectValue("coverageStartDate", "required");
            }
        }
    }

    @Data
    public static class InsurancePolicyModel {

        private Integer policyId;
        private Patient owner;
        private Integer insuranceId;
        private String insuranceCardNo;
        private Date coverageStartDate;
        private Date expirationDate;
        private Integer thirdPartyId;

        public InsurancePolicyModel() {}

        public InsurancePolicyModel(InsurancePolicy policy) {
            this.policyId = policy.getInsurancePolicyId();
            this.owner = policy.getOwner();
            this.insuranceId = policy.getInsurance() == null ? null : policy.getInsurance().getInsuranceId();
            this.insuranceCardNo = policy.getInsuranceCardNo();
            this.coverageStartDate = policy.getCoverageStartDate();
            this.expirationDate = policy.getExpirationDate();
            this.thirdPartyId = policy.getThirdParty() == null ? null : policy.getThirdParty().getThirdPartyId();
        }

        public void validate(Errors errors) {

        }
    }
}
