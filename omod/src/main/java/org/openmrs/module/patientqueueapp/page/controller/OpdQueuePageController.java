package org.openmrs.module.patientqueueapp.page.controller;

import org.apache.commons.collections.CollectionUtils;
import org.openmrs.Concept;
import org.openmrs.ConceptAnswer;
import org.openmrs.User;
import org.openmrs.api.context.Context;
import org.openmrs.module.appframework.domain.AppDescriptor;
import org.openmrs.module.appui.UiSessionContext;
import org.openmrs.module.hospitalcore.util.ConceptAnswerComparator;
import org.openmrs.module.kenyaui.annotation.AppPage;
import org.openmrs.module.patientqueueapp.PatientQueueConstants;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.page.PageModel;
import org.openmrs.ui.framework.page.PageRequest;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;


/**
 *
 */
@AppPage(PatientQueueConstants.APP_OPD)
public class OpdQueuePageController {
    public String get(
            @RequestParam("app") AppDescriptor appDescriptor,
            UiSessionContext sessionContext,
            PageModel model,
            UiUtils ui,
            HttpSession session,
            PageRequest pageRequest
            ) {

        model.addAttribute("afterSelectedUrl", "/patientdashboardapp/main.page?patientId={{patientId}}&opdId={{opdId}}&queueId={{queueId}}");
        User usr = Context.getAuthenticatedUser();
        model.addAttribute("title", "OPD Queue");
        model.addAttribute("date", new Date());
        Concept opdWardConcept = Context.getConceptService().getConceptByUuid("03880388-07ce-4961-abe7-0e58f787dd23");
        Concept specialClinicConcept = Context.getConceptService().getConceptByUuid("b5e0cfd3-1009-4527-8e36-83b5e902b3ea");
        List<ConceptAnswer> patientList = new ArrayList<ConceptAnswer>();
        List<ConceptAnswer> opdList = (opdWardConcept != null
                ? new ArrayList<ConceptAnswer>(opdWardConcept.getAnswers()) : null);
        List<ConceptAnswer> specialClinicList = (specialClinicConcept != null
                ? new ArrayList<ConceptAnswer>(specialClinicConcept.getAnswers()) : null);
        patientList.addAll(specialClinicList);
        patientList.addAll(opdList);
        if (CollectionUtils.isNotEmpty( patientList)) {
            Collections.sort( patientList, new ConceptAnswerComparator());
        }
        model.addAttribute("listOPD",  patientList);
        return null;
    }
}
