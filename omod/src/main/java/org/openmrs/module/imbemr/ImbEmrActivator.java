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
package org.openmrs.module.imbemr;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.BaseModuleActivator;
import org.openmrs.module.ModuleActivator;

/**
 * This class contains the logic that is run every time this module is either started or stopped.
 */
public class ImbEmrActivator extends BaseModuleActivator {
	
	protected Log log = LogFactory.getLog(getClass());

	/**
	 * @see ModuleActivator#started()
	 */
	public void started() {
        log.warn("IMB EMR Module Started");
	}

	/**
	 * @see ModuleActivator#stopped()
	 */
	public void stopped() {
		log.info("IMB EMR Module stopped");
	}
}
