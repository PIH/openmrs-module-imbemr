package org.openmrs.module.imbemr;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Location;
import org.openmrs.LocationTag;
import org.openmrs.api.context.Context;
import org.openmrs.util.ConfigUtil;
import org.openmrs.util.PrivilegeConstants;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * This setup class ensures specific configurations are enabled on the server, and provides access to them
 */
public class ImbEmrUtil {

    protected static Log log = LogFactory.getLog(ImbEmrUtil.class);

    public static final String IMBEMR_SERVER_NAME = "imbemr.server_name";
    public static final String IMBEMR_SERVER_VISIT_LOCATIONS = "imbemr.server_visit_locations";

    public static String getConfigValue(String property) {
        return getConfigValue(property, null);
    }

    public static String getConfigValue(String property, String defaultValue) {
        try {
            Context.addProxyPrivilege(PrivilegeConstants.GET_GLOBAL_PROPERTIES);
            return ConfigUtil.getProperty(property, defaultValue);
        }
        finally {
            Context.removeProxyPrivilege(PrivilegeConstants.GET_GLOBAL_PROPERTIES);
        }
    }

    /**
     * @return a Location for the given lookup, first trying to parse to locationId, then trying to lookup by uuid
     */
    public static Location getLocation(String lookup) {
        Location l = null;
        if (StringUtils.isNotBlank(lookup)) {
            lookup = lookup.trim();
            try {
                l = Context.getLocationService().getLocation(Integer.parseInt(lookup));
            }
            catch (Exception ignored) {}
            if (l == null) {
                l = Context.getLocationService().getLocationByUuid(lookup);
            }
            if (l == null) {
                List<Location> locations = Context.getLocationService().getLocations(lookup);
                if (locations.size() == 1) {
                    l = locations.get(0);
                }
            }
        }
        return l;
    }

    protected static List<Location> getLocationsWithTag(Collection<Location> locations, String tag, boolean includeChildLocations) {
        List<Location> ret = new ArrayList<>();
        if (locations != null) {
            for (Location l : locations) {
                if (l.hasTag(tag)) {
                    ret.add(l);
                }
                if (includeChildLocations) {
                    ret.addAll(getLocationsWithTag(l.getChildLocations(), tag, includeChildLocations));
                }
            }
        }
        return ret;
    }

    public static List<Location> getVisitLocations() {
        List<Location> ret = new ArrayList<>();
        String configVal = getConfigValue(IMBEMR_SERVER_VISIT_LOCATIONS);
        if (StringUtils.isNotBlank(configVal)) {
            String[] locations = configVal.split(",");
            for (String lookup : locations) {
                Location location = getLocation(lookup);
                if (location == null) {
                    throw new IllegalStateException("Invalid location specified in " + IMBEMR_SERVER_VISIT_LOCATIONS + " -  + lookup");
                }
                if (!location.hasTag("Visit Location")) {
                    throw new IllegalStateException("Location configured as a server visit location is not tagged as a visit location: " + location.getName());
                }
                ret.add(location);
            }
        }
        else {
            LocationTag visitLocationTag = Context.getLocationService().getLocationTagByName("Visit Location");
            if (visitLocationTag == null) {
                throw new IllegalStateException("No Visit Location tag found");
            }
            ret.addAll(Context.getLocationService().getLocationsByTag(visitLocationTag));
        }
        if (ret.isEmpty()) {
            throw new IllegalStateException("No Visit Locations found");
        }
        return ret;
    }

    public static List<Location> getLoginLocations() {
        return getLocationsWithTag(getVisitLocations(), "Login Location", true);
    }
}
