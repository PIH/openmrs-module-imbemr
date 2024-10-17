package org.openmrs.module.imbemr.config;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.initializer.Domain;
import org.openmrs.module.initializer.api.ConfigDirUtil;
import org.openmrs.module.initializer.api.InitializerService;
import org.openmrs.module.initializer.api.loaders.BaseFileLoader;
import org.openmrs.module.initializer.api.loaders.Loader;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Custom loader for initializer configurations which enables us to do some things that iniz does not do by default
 * 1. Fail hard if there are any errors, rather than ignore them
 * 2. Install configurations at the time when we are ready, not in the Iniz activator
 * 3. Install only some configurations based on how the current server is configured
 */
public class InitializerSetup {

    protected static Log log = LogFactory.getLog(InitializerSetup.class);

    public static List<Domain> ALWAYS_RELOAD = Collections.singletonList(Domain.LOCATION_TAG_MAPS);

    public static void install() {
        try {
            for (Domain domain : ALWAYS_RELOAD) {
                log.warn("Deleting checksums to force reloading of: " + domain);
                deleteChecksumsForDomains(domain);
            }
            for (Loader loader : getInitializerService().getLoaders()) {
                log.warn("Loading from Initializer: " + loader.getDomainName());
                List<String> exclusionsForLoader = getExclusionsForLoader(loader);
                loader.loadUnsafe(exclusionsForLoader, true);
            }
        }
        catch (Exception e) {
            throw new IllegalStateException("An error occurred while loading from initializer", e);
        }
    }

    /**
     * The purpose of this method is to determine if there are any site-specific configuration files,
     * and if so, to exclude them if they are not intended for the specific config in use.
     * Any config files that contain a "-site-", and which do not end with the site name, are excluded
     */
    public static List<String> getExclusionsForLoader(Loader loader) {
        List<String> exclusions = new ArrayList<>();
        if (loader instanceof BaseFileLoader) {
            BaseFileLoader ll = (BaseFileLoader) loader;
            String site = ServerSetup.getServerName();
            for (File f : ll.getDirUtil().getFiles("csv")) {
                String filename = f.getName().toLowerCase();
                if (filename.contains("-site-") && !filename.endsWith("-site-" + site + ".csv")) {
                    log.debug("Excluding site-specific configuration file: " + filename);
                    exclusions.add(filename);
                }
            }
        }
        return exclusions;
    }

    /**
     * Deletes the checksum files for the given domains
     * @param domains the domains for which to delete the checksum files
     */
    public static void deleteChecksumsForDomains(Domain... domains) {
        String configDirPath = getInitializerService().getConfigDirPath();
        String checksumsDirPath = getInitializerService().getChecksumsDirPath();
        for (Domain domain : domains) {
            ConfigDirUtil util = new ConfigDirUtil(configDirPath, checksumsDirPath, domain.getName(), false);
            util.deleteChecksums();
        }
    }

    protected static InitializerService getInitializerService() {
        return Context.getService(InitializerService.class);
    }
}
