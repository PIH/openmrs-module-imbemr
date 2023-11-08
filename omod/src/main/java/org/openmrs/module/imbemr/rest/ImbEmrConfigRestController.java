package org.openmrs.module.imbemr.rest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.messagesource.MessageSourceService;
import org.openmrs.messagesource.MutableMessageSource;
import org.openmrs.module.ModuleFactory;
import org.openmrs.module.imbemr.ImbEmrAppLoaderFactory;
import org.openmrs.module.initializer.InitializerMessageSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import static org.springframework.http.HttpStatus.OK;

@Controller
public class ImbEmrConfigRestController {

    protected final Log log = LogFactory.getLog(getClass());

    @Autowired
    ImbEmrAppLoaderFactory imbEmrAppLoaderFactory;

    @Autowired
    MessageSourceService messageSourceService;

    private static final String REQUIRED_PRIVILEGE = "App: coreapps.systemAdministration";

    @RequestMapping(value = "/rest/v1/imbemr/config/appframework", method = RequestMethod.PUT)
    @ResponseBody
    public Object updateAppsAndExtensions() {
        log.warn("Refreshing apps and extensions");
        if (!Context.hasPrivilege(REQUIRED_PRIVILEGE)) {
            log.error("User does not have sufficient privileges: " + REQUIRED_PRIVILEGE);
            return HttpStatus.UNAUTHORIZED;
        }
        ModuleFactory.getStartedModuleById("appframework").getModuleActivator().contextRefreshed();
        log.warn("Successfully refreshed apps and extensions");
        return OK;
    }

    @RequestMapping(value = "/rest/v1/imbemr/config/messageproperties", method = RequestMethod.PUT)
    @ResponseBody
    public Object updateMessageProperties() {
        log.warn("Refreshing message properties");
        if (!Context.hasPrivilege(REQUIRED_PRIVILEGE)) {
            log.error("User does not have sufficient privileges: " + REQUIRED_PRIVILEGE);
            return HttpStatus.UNAUTHORIZED;
        }
        try {
            MutableMessageSource messageSource = Context.getMessageSourceService().getActiveMessageSource();
            if (messageSource instanceof InitializerMessageSource) {
                InitializerMessageSource initializerMessageSource = (InitializerMessageSource) messageSource;
                initializerMessageSource.refreshCache();
                log.warn("Successfully refreshed message properties");
                return OK;
            }
            log.warn("Message source is not Initializer Message Source, unable to refresh");
            return HttpStatus.FAILED_DEPENDENCY;
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}