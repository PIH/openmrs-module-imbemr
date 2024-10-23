package org.openmrs.module.imbemr.config;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.util.ConfigUtil;

/**
 * This setup class ensures specfic configurations are enabled on the server, and provides access to them
 */
public class ServerSetup {

    protected static Log log = LogFactory.getLog(ServerSetup.class);

    public static final String IMBEMR_SERVER_NAME = "imbemr.server_name";

    public static void setup() {
        setupServerName();
    }

    public static void setupServerName() {
        String serverName = getServerName();
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

    public static String getServerName() {
        return ConfigUtil.getGlobalProperty(IMBEMR_SERVER_NAME);
    }
}
