package org.openmrs.module.patientqueueapp.page.controller;

import org.openmrs.Concept;
import org.openmrs.api.context.Context;
import org.openmrs.module.appui.UiSessionContext;
import org.openmrs.module.kenyaui.annotation.AppPage;
import org.openmrs.module.patientqueueapp.PatientQueueConstants;
import org.openmrs.module.patientqueueapp.PatientQueueUtils;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.page.PageModel;
import org.openmrs.ui.framework.page.PageRequest;

import javax.servlet.http.HttpSession;
import java.util.Date;

@AppPage(PatientQueueConstants.APP_ONCOLOGY_CLINIC)
public class TreatmentProgramPageController {
    private static final String ONCOLOGY_CLINIC_CONCEPT_UUID = "1acb3707-9e03-40e3-b157-ce28451c3fd0";//mch clinic
    public String get(
            UiSessionContext sessionContext,
            PageModel model,
            HttpSession session,
            PageRequest pageRequest,
            UiUtils ui
    ) {
        Concept mchClinicConcept = Context.getConceptService().getConceptByUuid(ONCOLOGY_CLINIC_CONCEPT_UUID);
        Integer mchClinicConceptId = mchClinicConcept.getConceptId();
        model.addAttribute("programId",mchClinicConceptId);
        model.addAttribute("mchQueueRoles", PatientQueueUtils.getTreatmentProgramUserRoles(ui, "Clinic"));
        model.addAttribute("date", new Date());
        model.addAttribute("fptabIncludedInPNC", Context.getAdministrationService().getGlobalProperty("fptab.includedInPNC"));
        return null;
    }
}
