package org.openmrs.module.imbemr.rest;

import org.openmrs.api.context.Context;
import org.openmrs.module.ModuleFactory;
import org.openmrs.module.imbemr.ImbEmrAppLoaderFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import static org.springframework.http.HttpStatus.OK;

@Controller
public class ImbEmrConfigRestController {

    @Autowired
    ImbEmrAppLoaderFactory imbEmrAppLoaderFactory;

    private static String REQUIRED_PRIVILEGE = "App: coreapps.systemAdministration";

    @RequestMapping(value = "/rest/v1/imbemr/config/appframework", method = RequestMethod.PUT)
    @ResponseBody
    public Object updateAppsAndExtensions() {
        if (!Context.hasPrivilege(REQUIRED_PRIVILEGE)) {
            return HttpStatus.UNAUTHORIZED;
        }
        ModuleFactory.getStartedModuleById("appframework").getModuleActivator().contextRefreshed();
        return OK;
    }
}