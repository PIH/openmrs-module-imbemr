/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.imbemr.page.controller.admin;

import org.apache.commons.lang.StringUtils;
import org.openmrs.Location;
import org.openmrs.LocationTag;
import org.openmrs.api.LocationService;
import org.openmrs.module.appui.UiSessionContext;
import org.openmrs.module.imbemr.LocationTagUtil;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.page.PageModel;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controls page which is used to set the user's current session location
 */
@Controller
public class ConfigureLoginLocationsPageController {

    public void get(PageModel model, UiUtils ui, UiSessionContext sessionContext,
                    @RequestParam(name = "systemType", required = false) String systemType,
                    @SpringBean("locationService") LocationService locationService,
                    @SpringBean LocationTagUtil locationTagUtil) {

        if (StringUtils.isBlank(systemType)) {
            systemType = locationTagUtil.getConfiguredSystemType();
        }
        model.addAttribute("systemType", systemType);
        model.addAttribute("locationTagUtil", locationTagUtil);
        model.addAttribute("allLocations", locationService.getAllLocations());
        model.addAttribute("authenticatedUser", sessionContext.getCurrentUser());
    }

    public String post(PageModel model, UiUtils ui, UiSessionContext sessionContext,
                       @SpringBean LocationTagUtil locationTagUtil,
                       @SpringBean("locationService") LocationService locationService,
                       @RequestParam("visitLocations") List<Location> visitLocations,
                       @RequestParam("systemType") String systemType) {

        try {
            if (LocationTagUtil.SINGLE_LOCATION.equals(systemType)) {
                if (visitLocations.size() != 1) {
                    throw new IllegalArgumentException("Exactly one visit location must be provided");
                }
                configureSingleLocation(locationService, locationTagUtil, visitLocations.get(0));
            }
        }
        catch (Exception e) {
            // TODO: Gather error messages for display
        }

        Map<String, Object> params = new HashMap<>();
        params.put("systemType", systemType);
        return "redirect:" + ui.pageLink("imbemr", "admin/configureLoginLocations", params);
    }

    // TODO: Make transactional, move into a service
    protected void configureSingleLocation(LocationService locationService, LocationTagUtil locationTagUtil, Location location) {
        LocationTag visitLocationTag = locationTagUtil.getVisitLocationTag();
        LocationTag loginLocationTag = locationTagUtil.getLoginLocationTag();
        for (Location l : locationService.getAllLocations()) {
            if (l.equals(location)) {
                l.addTag(visitLocationTag);
                l.addTag(loginLocationTag);
            }
            else {
                l.removeTag(visitLocationTag);
                l.removeTag(loginLocationTag);
            }
            locationService.saveLocation(l);
        }
    }
}
