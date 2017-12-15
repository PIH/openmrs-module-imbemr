package org.openmrs.module.imbemr.fragment.controller;


import org.openmrs.module.imbemr.ImbEmrWebConstants;
import org.openmrs.ui.framework.fragment.FragmentModel;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class InfoAndErrorMessagesFragmentController {

    public void controller(HttpServletRequest request, FragmentModel fragmentModel) {
        HttpSession session = request.getSession();
        String errorMessage = (String) session
                .getAttribute(ImbEmrWebConstants.SESSION_ATTRIBUTE_ERROR_MESSAGE);
        String infoMessage = (String) session.getAttribute(ImbEmrWebConstants.SESSION_ATTRIBUTE_INFO_MESSAGE);
        session.setAttribute(ImbEmrWebConstants.SESSION_ATTRIBUTE_ERROR_MESSAGE, null);
        session.setAttribute(ImbEmrWebConstants.SESSION_ATTRIBUTE_INFO_MESSAGE, null);
        fragmentModel.addAttribute("errorMessage", errorMessage);
        fragmentModel.addAttribute("infoMessage", infoMessage);
    }

}
