package org.openmrs.module.imbemr.htmlformentry;

import org.apache.velocity.VelocityContext;
import org.openmrs.module.htmlformentry.FormEntrySession;
import org.openmrs.module.htmlformentry.velocity.VelocityContextContentProvider;
import org.openmrs.module.mohappointment.model.Services;
import org.openmrs.module.mohappointment.utils.AppointmentUtil;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ImbEmrVelocityContextProvider implements VelocityContextContentProvider {

    @Override
    public void populateContext(FormEntrySession formEntrySession, VelocityContext velocityContext) {

        // Add in appointment services
        List<String> appointmentServiceConceptUuids = new ArrayList<>();
        List<String> appointmentServiceNames = new ArrayList<>();
        for (Services service : AppointmentUtil.getAllServices()) {
            if (service.getConcept() != null) {
                appointmentServiceConceptUuids.add(service.getConcept().getUuid());
                appointmentServiceNames.add(service.getName());
            }
        }
        formEntrySession.addToVelocityContext("appointmentServiceConceptUuids", String.join(",", appointmentServiceConceptUuids));
        formEntrySession.addToVelocityContext("appointmentServiceConceptNames", String.join(",", appointmentServiceNames));
    }
}
