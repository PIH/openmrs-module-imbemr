package org.openmrs.module.imbemr.search;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Patient;
import org.openmrs.module.namephonetics.NamePhoneticsService;
import org.openmrs.module.registrationcore.api.search.PatientAndMatchQuality;
import org.openmrs.module.registrationcore.api.search.SimilarPatientSearchAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service("imbemr.RwandaFastPatientSearchAlgorithm")
public class RwandaFastPatientSearchAlgorithm implements SimilarPatientSearchAlgorithm {

    protected static final Log log = LogFactory.getLog(RwandaFastPatientSearchAlgorithm.class);

    @Autowired
    NamePhoneticsService namePhoneticsService;

    @Override
    public List<PatientAndMatchQuality> findSimilarPatients(Patient search, Map<String, Object> otherDataPoints, Double cutoff, Integer maxResults) {
        List<PatientAndMatchQuality> ret = new ArrayList<>();

        String givenName = search.getGivenName();
        String familyName = search.getFamilyName();
        String gender = search.getGender();

        // Only execute searches if at least the given and family names are entered
        if (StringUtils.isNotBlank(givenName) && StringUtils.isNotBlank(familyName)) {
            for (Patient candidate : getPhoneticsMatches(givenName, familyName)) {
                List<String> matchedFields = Arrays.asList("names.givenName", "names.familyName");
                // Only match if gender is not yet entered, or if it matches exactly
                if (StringUtils.isBlank(gender) || gender.equals(candidate.getGender())) {
                    if (gender != null && gender.equals(candidate.getGender())) {
                        matchedFields.add("gender");
                    }
                    ret.add(new PatientAndMatchQuality(candidate, cutoff, matchedFields));
                }
            }
        }

        // TODO: We likely want to further refine here based on address, person attributes, phone number, national id, etc
        return ret;
    }

    public Set<Patient> getPhoneticsMatches(String givenName, String familyName) {
        log.trace("Searching phonetic matches for " + givenName + " " + familyName);
        Set<Patient> ret = new HashSet<>(namePhoneticsService.findPatient(givenName, null, familyName, null));
        // TODO: This matches the existing primary care behavior, but do we want to always search both directions?
        if (ret.isEmpty()) {
            ret.addAll(namePhoneticsService.findPatient(familyName, null, givenName, null));
        }
        log.trace("Phonetics result count: " + ret.size());
        return ret;
    }
}
