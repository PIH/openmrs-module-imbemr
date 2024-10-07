package org.openmrs.module.imbemr.page.controller.patient;

import lombok.Data;
import org.apache.commons.lang.StringUtils;
import org.openmrs.Patient;
import org.openmrs.User;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.messagesource.MessageSourceService;
import org.openmrs.module.emrapi.patient.PatientDomainWrapper;
import org.openmrs.module.mohbilling.businesslogic.InsurancePolicyUtil;
import org.openmrs.module.mohbilling.businesslogic.InsuranceUtil;
import org.openmrs.module.mohbilling.model.Beneficiary;
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

        Date now = new Date();
        User currentUser = Context.getAuthenticatedUser();

        try {
            if (!Context.hasPrivilege("Create Insurance Policy")) {
                throw new APIException(mss.getMessage("require.unauthorized"));
            }

            // TODO: Review this, but 1.x billing module code does not allow any duplicate card numbers, even across insurance types
            InsurancePolicy existingPolicy = Context.getService(BillingService.class).getInsurancePolicyByCardNo(policyModel.getInsuranceCardNo());
            if (existingPolicy != null && !existingPolicy.getInsurancePolicyId().equals(policy.getInsurancePolicyId())) {
                errors.rejectValue("insuranceCardNo", "imbemr.insurance.error.duplicateCardNumber");
            }

            policy.setInsurance(InsuranceUtil.getInsurance(policyModel.getInsuranceId()));
            policy.setOwner(policyModel.getOwner());
            policy.setInsuranceCardNo(policyModel.getInsuranceCardNo());
            policy.setCoverageStartDate(policyModel.getCoverageStartDate());
            policy.setExpirationDate(policyModel.getExpirationDate());
            policy.setThirdParty(policyModel.getThirdPartyId() == null ? null : InsurancePolicyUtil.getThirdParty(policyModel.getThirdPartyId()));
            policy.setCreatedDate(now);
            policy.setCreator(currentUser);
            policy.setRetired(false); // TODO: Support retiring and un-retiring?

            Beneficiary beneficiary = null;
            if (policy.getBeneficiaries() != null) {
                for (Beneficiary b : policy.getBeneficiaries()) {
                    beneficiary = b;
                }
            }
            if (beneficiary == null) {
                beneficiary = new Beneficiary();
                beneficiary.setPatient(policy.getOwner());
                beneficiary.setInsurancePolicy(policy);
                beneficiary.setPolicyIdNumber(policy.getInsuranceCardNo());
                beneficiary.setCreatedDate(now);
                beneficiary.setCreator(currentUser);
                beneficiary.setRetired(false);  // TODO: Support retiring and un-retiring?
                policy.addBeneficiary(beneficiary);
            }

            // TODO: There is some odd logic about how these are set in 1.x.  Review that or this for correctness.
            beneficiary.setOwnerName(policyModel.getOwnerName());
            beneficiary.setOwnerCode(policyModel.getOwnerCode());
            beneficiary.setLevel(policyModel.getLevel());
            beneficiary.setCompany(policyModel.getCompany());

            InsurancePolicyValidator validator = new InsurancePolicyValidator();
            validator.validate(policy, errors);

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

    public List<Patient> getEligiblePolicyOwnersForPatient(Patient patient) {
        List<Patient> ret = new ArrayList<>();
        ret.add(patient);
        // TODO: Should we support owners other than the patient?  Maybe from relationships?
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
        private String ownerName;
        private String ownerCode;
        private Integer level;
        private String company;

        public InsurancePolicyModel() {}

        public InsurancePolicyModel(InsurancePolicy policy) {
            this.policyId = policy.getInsurancePolicyId();
            this.owner = policy.getOwner();
            this.insuranceId = policy.getInsurance() == null ? null : policy.getInsurance().getInsuranceId();
            this.insuranceCardNo = policy.getInsuranceCardNo();
            this.coverageStartDate = policy.getCoverageStartDate();
            this.expirationDate = policy.getExpirationDate();
            this.thirdPartyId = policy.getThirdParty() == null ? null : policy.getThirdParty().getThirdPartyId();

            // TODO: This is how things are done in the 1.x code.  Only the owner is supported as a beneficiary.
            if (policy.getBeneficiaries() != null) {
                for (Beneficiary beneficiary : policy.getBeneficiaries()) {
                    this.ownerName = beneficiary.getOwnerName();
                    this.ownerCode = beneficiary.getOwnerCode();
                    this.level = beneficiary.getLevel();
                    this.company = beneficiary.getCompany();
                }
            }
        }
    }
}
