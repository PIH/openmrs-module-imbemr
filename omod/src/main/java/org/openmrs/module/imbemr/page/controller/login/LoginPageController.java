/*
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

package org.openmrs.module.imbemr.page.controller.login;

import org.apache.commons.lang.StringUtils;
import org.openmrs.Location;
import org.openmrs.api.context.Context;
import org.openmrs.module.authentication.web.AuthenticationSession;
import org.openmrs.module.authenticationui.AuthenticationUiConfig;
import org.openmrs.module.imbemr.ImbEmrUtil;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.page.PageModel;
import org.openmrs.ui.framework.page.PageRequest;

import java.util.ArrayList;
import java.util.List;

import static org.openmrs.module.uicommons.UiCommonsConstants.SESSION_ATTRIBUTE_ERROR_MESSAGE;

public class LoginPageController {

	public String get(
			PageModel pageModel, UiUtils ui,
			@SpringBean("authenticationUiConfig") AuthenticationUiConfig authenticationUiConfig,
			PageRequest request) {

		AuthenticationSession authenticationSession = new AuthenticationSession(request.getRequest(), request.getResponse());
		if (Context.isAuthenticated()) {
			return "redirect:" + authenticationUiConfig.getHomePageUrl(ui);
		}
		else {
			authenticationSession.getHttpSession().removeAttribute(AuthenticationSession.AUTHENTICATION_USER_LOGIN);
		}

		List<Location> loginLocations = new ArrayList<>();
		if (authenticationUiConfig.isShowLoginLocations()) {
			try {
				loginLocations = ImbEmrUtil.getLoginLocations();
			}
			catch (Exception e) {
				authenticationSession.getHttpSession().setAttribute(SESSION_ATTRIBUTE_ERROR_MESSAGE, e.getMessage());
			}
		}

		Location lastLoginLocation = null;
		String cookieName = authenticationUiConfig.getLastLocationCookieName();
		if (StringUtils.isNotBlank(cookieName)) {
			String lastSessionLocationId = request.getCookieValue(cookieName);
			if (StringUtils.isNotBlank(lastSessionLocationId)) {
				for (Location location : loginLocations) {
					if (location.getId().toString().equals(lastSessionLocationId)) {
						lastLoginLocation = location;
					}
				}
			}
		}

		pageModel.put("authenticationUiConfig", authenticationUiConfig);
		pageModel.put("authenticationSession", authenticationSession);
		pageModel.addAttribute("locations", loginLocations);
		pageModel.addAttribute("lastSessionLocation", lastLoginLocation);
		return null;
	}

	public String post(UiUtils ui, @SpringBean("authenticationUiConfig") AuthenticationUiConfig authenticationUiConfig) {
		return "redirect:" + authenticationUiConfig.getHomePageUrl(ui);
	}
}
