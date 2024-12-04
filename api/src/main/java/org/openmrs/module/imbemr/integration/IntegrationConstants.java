package org.openmrs.module.imbemr.integration;

import org.openmrs.module.imbemr.ImbEmrConstants;

import java.util.HashMap;
import java.util.Map;

public class IntegrationConstants {

    public static final String NID = "NID";
    public static final String NID_APPLICATION_NUMBER = "NID_APPLICATION_NUMBER";
    public static final String NIN = "NIN";
    public static final String UPI = "UPI";
    public static final String PASSPORT = "PASSPORT";

    public static final Map<String, String> NIDA_IDENTIFIER_SYSTEMS = new HashMap<>();
    static {
        NIDA_IDENTIFIER_SYSTEMS.put(NID, ImbEmrConstants.NATIONAL_ID_UUID);
        NIDA_IDENTIFIER_SYSTEMS.put(NID_APPLICATION_NUMBER, ImbEmrConstants.NID_APPLICATION_NUMBER_UUID);
        NIDA_IDENTIFIER_SYSTEMS.put(NIN, ImbEmrConstants.NIN_UUID);
        NIDA_IDENTIFIER_SYSTEMS.put(UPI, ImbEmrConstants.UPID_UUID);
        NIDA_IDENTIFIER_SYSTEMS.put(PASSPORT, ImbEmrConstants.PASSPORT_NUMBER_UUID);
    }

}
