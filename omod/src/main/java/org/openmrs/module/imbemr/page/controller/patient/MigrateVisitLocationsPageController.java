package org.openmrs.module.imbemr.page.controller.patient;

import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.emrapi.patient.PatientDomainWrapper;
import org.openmrs.module.imbemr.migrations.VisitLocationMigrator;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.annotation.InjectBeans;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.page.PageModel;
import org.openmrs.util.PrivilegeConstants;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

public class MigrateVisitLocationsPageController {

    public String get(PageModel model,
                      @InjectBeans PatientDomainWrapper patientDomainWrapper,
                      @RequestParam(value = "patientId") Patient patient,
                      @SpringBean VisitLocationMigrator visitLocationMigrator) {

        if (!Context.hasPrivilege(PrivilegeConstants.EDIT_VISITS)) {
            return "redirect:/index.htm";
        }

        patientDomainWrapper.setPatient(patient);
        model.addAttribute("patient", patientDomainWrapper);
        model.addAttribute("visitsToMigrate", visitLocationMigrator.getVisitsThatRequireLocationMigration(patient));
        return "patient/migrateVisitLocations";
    }

    public String post(PageModel model, UiUtils ui, HttpServletRequest request,
                       @InjectBeans PatientDomainWrapper patientDomainWrapper,
                       @RequestParam(value = "patientId") Patient patient,
                       @SpringBean VisitLocationMigrator visitLocationMigrator) {

        patientDomainWrapper.setPatient(patient);

        try {
            visitLocationMigrator.migrateVisitLocationsForPatient(patient);
        }
        catch (Exception e) {
            request.getSession().setAttribute("emr.errorMessage", e.getMessage());
            model.addAttribute("patient", patientDomainWrapper);
            model.addAttribute("visitsToMigrate", visitLocationMigrator.getVisitsThatRequireLocationMigration(patient));
            return "patient/migrateVisitLocations";
        }
        request.getSession().setAttribute("emr.infoMessage", "Visits migrated successfully");
        request.getSession().setAttribute("emr.toastMessage", "true");
        Map<String, Object> params = new HashMap<>();
        params.put("patientId", patient.getUuid());
        return "redirect:" + ui.pageLink("coreapps", "clinicianfacing/patient", params);
    }
}
