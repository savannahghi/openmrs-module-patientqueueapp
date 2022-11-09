package org.openmrs.module.patientqueueapp;

import org.openmrs.Patient;
import org.openmrs.Role;
import org.openmrs.api.context.Context;
import org.openmrs.module.treatmentapp.api.TreatmentService;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.ui.framework.UiUtils;

import java.util.ArrayList;
import java.util.List;

public class PatientQueueUtils {
    public static final String PROGRAM_USER_CHEMO_QUEUE = "f8ff74bd-e776-4025-a7d5-aa6c40b498a1";
    public static final String PROGRAM_USER_RADIO_QUEUE = "2136bf9a-18b7-4179-858f-30c7cba191de";
    public static final String PROGRAM_USER_SURGERY_QUEUE = "e2d5977d-2b92-4b39-b2c9-63bf0d21e8f2";

    public static final String EXAM_ROOM_CONCEPT_UUID = "11303942-75cd-442a-aead-ae1d2ea9b3eb";

    public static List<SimpleObject> getTreatmentProgramUserRoles(UiUtils ui, String clinic) {
        List<Role> roles = new ArrayList<Role>(Context.getAuthenticatedUser().getAllRoles());
        List<Role> mchRoles = new ArrayList<Role>();

        for (Role role : roles) {
            if (clinic.equals("Clinic")){
                if (role.getUuid().equals(PROGRAM_USER_CHEMO_QUEUE) || role.getUuid().equals(PROGRAM_USER_RADIO_QUEUE) || role.getUuid().equals(PROGRAM_USER_SURGERY_QUEUE)) {
                    mchRoles.add(role);
                }
            }
        }
        return SimpleObject.fromCollection(mchRoles, ui, "role", "description", "uuid");
    }
    public static String enrolledTreatmentProgram(Patient patient)
    {
        TreatmentService programService = Context.getService(TreatmentService.class);
        if(programService.enrolledInChemo(patient)){
            return("CHEMO");
        }
        else if(programService.enrolledInRadioTherapy(patient)){
            return("RADIOTHERAPY");
        }
        else if(programService.enrolledInSurgery(patient)){
            return("SURGERY");
        }
        else{
            return("N/A");
        }
    }
}
