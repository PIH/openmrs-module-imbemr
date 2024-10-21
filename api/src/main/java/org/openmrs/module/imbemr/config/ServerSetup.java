package org.openmrs.module.imbemr.config;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Location;
import org.openmrs.api.context.Context;
import org.openmrs.module.imbemr.ImbEmrUtil;
import org.openmrs.util.ConfigUtil;

import static org.openmrs.module.imbemr.ImbEmrUtil.IMBEMR_SERVER_NAME;
import static org.openmrs.module.imbemr.ImbEmrUtil.IMBEMR_SERVER_VISIT_LOCATIONS;

/**
 * This setup class ensures specific configurations are enabled on the server, and provides access to them
 */
public class ServerSetup {

    protected static Log log = LogFactory.getLog(ServerSetup.class);

    public static void setup() {
        setupServerName();
        setupServerVisitLocations();
    }

    public static void setupServerName() {
        String serverName = ImbEmrUtil.getConfigValue(IMBEMR_SERVER_NAME);
        if (StringUtils.isBlank(serverName)) {
            log.info("Setting up new: " + IMBEMR_SERVER_NAME);
            serverName = ConfigUtil.getGlobalProperty("sync.server_name");
            if (StringUtils.isBlank(serverName)) {
                throw new IllegalStateException("Unable to setup the server name for this instance.  Please configure the " + IMBEMR_SERVER_NAME + " property.");
            }
            serverName = serverName.trim().replace(" ", "_").toLowerCase();
            Context.getAdministrationService().setGlobalProperty(IMBEMR_SERVER_NAME, serverName);
            log.warn(IMBEMR_SERVER_NAME + " set to: " + serverName);
        }
        log.info(IMBEMR_SERVER_NAME + " = " + serverName);
    }

    public static void setupServerVisitLocations() {
        String value = ImbEmrUtil.getConfigValue(IMBEMR_SERVER_VISIT_LOCATIONS);
        if (StringUtils.isBlank(value)) {
            log.info("Setting up new: " + IMBEMR_SERVER_VISIT_LOCATIONS);
            value = ConfigUtil.getGlobalProperty("registration.defaultLocationCode");
            if (StringUtils.isBlank(value)) {
                value = ConfigUtil.getGlobalProperty("billing.defaultLocation");
            }
            if (StringUtils.isBlank(value)) {
                value = ConfigUtil.getGlobalProperty("mohtracportal.defaultLocationId");
            }
            if (StringUtils.isBlank(value)) {
                throw new IllegalStateException("Unable to setup this instance.  Please configure the " + IMBEMR_SERVER_VISIT_LOCATIONS + " property.");
            }
            try {
                Location location = Context.getLocationService().getLocation(Integer.parseInt(value));
                if (location == null) {
                    throw new IllegalStateException("No default location found for: " + value);
                }
                if (!location.hasTag("Visit Location")) {
                    throw new IllegalStateException("Default location is not configured as a visit location: " + location.getName());
                }
                value = location.getUuid();
            }
            catch (Exception e) {
                throw new IllegalStateException("Invalid default location specified: " + value, e);
            }
            Context.getAdministrationService().setGlobalProperty(IMBEMR_SERVER_VISIT_LOCATIONS, value);
            log.warn(IMBEMR_SERVER_VISIT_LOCATIONS + " set to: " + value);
        }
        log.info(IMBEMR_SERVER_VISIT_LOCATIONS + " = " + value);
    }
}
