package org.openmrs.module.imbemr.migrations;

import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.Visit;
import org.openmrs.api.VisitService;
import org.openmrs.module.emrapi.adt.AdtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class exists to migrate visit locations where possible to ensure the locations
 * associated with Visits are valid Visit Locations
 */
@Component
public class VisitLocationMigrator {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private final VisitService visitService;

    private final AdtService adtService;

    public VisitLocationMigrator(@Autowired VisitService visitService, @Autowired AdtService adtService) {
        this.visitService = visitService;
        this.adtService = adtService;
    }

    public Map<Visit, Location> getVisitsThatRequireLocationMigration(Patient patient) {
        Map<Visit, Location> ret = new HashMap<>();
        List<Location> visitLocations = adtService.getAllLocationsThatSupportVisits();
        List<Visit> visits = visitService.getVisitsByPatient(patient);
        for (Visit visit : visits) {
            Location existingVisitLocation = visit.getLocation();
            Location correctVisitLocation = getVisitLocation(existingVisitLocation, visitLocations);
            if (correctVisitLocation != null && !correctVisitLocation.equals(existingVisitLocation)) {
                ret.put(visit, correctVisitLocation);
            }
        }
        return ret;
    }

    public void migrateVisitLocationsForPatient(Patient patient) {
        Map<Visit, Location> migrationRequired = getVisitsThatRequireLocationMigration(patient);
        if (migrationRequired.isEmpty()) {
            log.debug("Patient " + patient.getUuid() + " does not have any visits that require migration");
            return;
        }
        log.warn("Patient " + patient.getUuid() + " has " + migrationRequired.size() + " visits that require migration");
        for (Visit visit : migrationRequired.keySet()) {
            migrationVisitLocation(visit, migrationRequired.get(visit));
        }
        log.warn("Migration completed for patient");
    }

    public void migrationVisitLocation(Visit visit, Location correctLocation) {
        log.warn("Updating location for visit " + visit.getUuid() + " from " + getLocationName(visit.getLocation()) + " to " + getLocationName(correctLocation));
        visit.setLocation(correctLocation);
        visitService.saveVisit(visit);
    }

    public Location getVisitLocation(Location location, List<Location> allowedLocations) {
        if (allowedLocations.contains(location)) {
            return location;
        }
        if (location.getParentLocation() != null) {
            return getVisitLocation(location.getParentLocation(), allowedLocations);
        }
        return null;
    }

    private String getLocationName(Location location) {
        return location == null ? "null" : location.getName();
    }
}
