/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 * <p>
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 * <p>
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.patientqueueapp.metadata;

import org.openmrs.module.kenyaemr.metadata.SecurityMetadata;
import org.openmrs.module.metadatadeploy.bundle.AbstractMetadataBundle;
import org.openmrs.module.metadatadeploy.bundle.Requires;
import org.springframework.stereotype.Component;
import org.openmrs.module.patientqueueapp.PatientQueueConstants;

import static org.openmrs.module.metadatadeploy.bundle.CoreConstructors.*;

/**
 * Implementation of access control to the app.
 */
@Component
@Requires(org.openmrs.module.kenyaemr.metadata.SecurityMetadata.class)
public class PatientQueueAppMetadata extends AbstractMetadataBundle {

    /**
     * Application IDs
     */

    public static final class _Privilege {
        public static final String OPD_MODULE_APP = "App: patientqueueapp.opdqueue";
        public static final String TRIAGE_MODULE_APP = "App: patientqueueapp.triage";
        public static final String MCH_TRIAGE_APP = "App: patientqueueapp.mchtriage";
        public static final String DELIVERY_APP = "App: patientqueueapp.delivery";
        public static final String MATERNITY_TRIAGE_APP = "App: patientqueueapp.maternitytriage";
        public static final String MCH_CLINIC_APP = "App: patientqueueapp.mchclinic";
        public static final String MCH_PAC_APP = "App: patientqueueapp.pac";

    }

    public static final class _Role {

        public static final String OPD = "Doctor";

        public static final String TRIAGE = "Nurse";

        public static final String MATERNITY = "Maternity App";

        public static final String DELIVERY = "Delivery App";

        public static final String PAC_ROOM = "Pac App";


    }

    /**
     * @see AbstractMetadataBundle#install()
     */
    @Override
    public void install() {
        install(privilege(_Privilege.OPD_MODULE_APP, "Able to access EHR OPD module features"));
        install(privilege(_Privilege.TRIAGE_MODULE_APP, "Able to access EHR triage module features"));
        install(privilege(_Privilege.MCH_TRIAGE_APP, "Able to access EHR MCH TRIAGE app features"));
        install(privilege(_Privilege.DELIVERY_APP, "Able to access EHR Delivery app features"));
        install(privilege(_Privilege.MATERNITY_TRIAGE_APP, "Able to access EHR Maternity triage app features"));
        install(privilege(_Privilege.MCH_CLINIC_APP, "Able to access EHR MCH Clinic app features"));
        install(privilege(_Privilege.MCH_PAC_APP, "Able to access EHR PAC app features"));

        install(role(_Role.OPD, "Can access Key EHR OPD module App",
                idSet(org.openmrs.module.kenyaemr.metadata.SecurityMetadata._Role.API_PRIVILEGES_VIEW_AND_EDIT),
                idSet(_Privilege.OPD_MODULE_APP, _Privilege.TRIAGE_MODULE_APP,_Privilege.MCH_TRIAGE_APP,_Privilege.DELIVERY_APP,_Privilege.MCH_PAC_APP,
                        _Privilege.MATERNITY_TRIAGE_APP,_Privilege.MCH_CLINIC_APP,_Privilege.MCH_PAC_APP)));

        install(role(_Role.TRIAGE, "Can access EHR Triage module App",
                idSet(org.openmrs.module.kenyaemr.metadata.SecurityMetadata._Role.API_PRIVILEGES_VIEW_AND_EDIT),
                idSet(_Privilege.TRIAGE_MODULE_APP, _Privilege.MCH_TRIAGE_APP, _Privilege.MATERNITY_TRIAGE_APP)));

        install(role(_Role.DELIVERY,"Can role for delivery app user",
                idSet(SecurityMetadata._Role.API_PRIVILEGES_VIEW_AND_EDIT),
                idSet(_Privilege.DELIVERY_APP,_Privilege.MATERNITY_TRIAGE_APP)));

        install(role(_Role.PAC_ROOM,"Role for Pac room user",
                idSet(SecurityMetadata._Role.API_PRIVILEGES_VIEW_AND_EDIT),
                idSet(_Privilege.MATERNITY_TRIAGE_APP,_Privilege.MCH_PAC_APP)));

        install(role(_Role.MATERNITY,"Role for Maternity room user",
                idSet(SecurityMetadata._Role.API_PRIVILEGES_VIEW_AND_EDIT),
                idSet(_Privilege.MATERNITY_TRIAGE_APP)));
    }
}