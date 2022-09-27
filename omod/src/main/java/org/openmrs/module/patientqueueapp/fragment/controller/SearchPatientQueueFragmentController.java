package org.openmrs.module.patientqueueapp.fragment.controller;

import org.apache.commons.lang.StringUtils;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.hospitalcore.HospitalCoreService;
import org.openmrs.module.hospitalcore.matcher.*;
import org.openmrs.module.patientqueueapp.PatientWrappers;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.ui.framework.UiUtils;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class SearchPatientQueueFragmentController {
    /**
     * The default controller method that handles the GET and POST requests if none is provided in this controller
     */
    public void controller() {
        //do nothing
    }

    public List<SimpleObject> searchPatient(
            @RequestParam(value = "phrase", required = false) String phrase,
            @RequestParam(value = "currentPage", required = false) Integer currentPage,
            @RequestParam(value = "pageSize", required = false) Integer pageSize,
            UiUtils uiUtils,
            HttpServletRequest request) {
        String gender = request.getParameter("gender");
        if (gender.equalsIgnoreCase("any")) {
            gender = null;
        }

        Integer age = getInt(request.getParameter("age"));
        Integer ageRange = getInt(request.getParameter("ageRange"));
        String relativeName = request.getParameter("relativeName");
        String lastDayOfVisit = request.getParameter("lastDayOfVisit");
        Integer lastVisitRange = getInt(request.getParameter("lastVisit"));
        String maritalStatus = request.getParameter("patientMaritalStatus");
        String phoneNumber = request.getParameter("phoneNumber");
        String nationalId = request.getParameter("nationalId");
        String fileNumber = request.getParameter("fileNumber");

        HospitalCoreService hcs = (HospitalCoreService) Context
                .getService(HospitalCoreService.class);
        List<Patient> patients = hcs.searchPatient(phrase, gender, age, ageRange, lastDayOfVisit, lastVisitRange, relativeName
                , maritalStatus, phoneNumber, nationalId, fileNumber
        );
        List<PatientWrappers> wrapperList = patientsWithLastVisit(patients);

        return SimpleObject.fromCollection(wrapperList, uiUtils, "patientId", "wrapperIdentifier", "names", "age", "gender", "formartedVisitDate", "voided", "dead", "lastVisitTime");
    }

    // Filter patient list using advance search criteria
    private List<Patient> filterPatients(HttpServletRequest request,
                                         List<Patient> patients) throws NumberFormatException,
            ParseException {

        List<Patient> filteredPatients = patients;

        // filter using gender
        String genderCriterion = request.getParameter("gender");
        if (!StringUtils.isBlank(genderCriterion)) {
            filteredPatients = select(filteredPatients, new GenderMatcher(
                    new String(genderCriterion)));
        }

        // filter using age criteria
        String ageCriterion = request.getParameter("age");
        if (!StringUtils.isBlank(ageCriterion)) {
            String ageRange = request.getParameter("ageRange");
            try {
                filteredPatients = select(filteredPatients, new AgeMatcher(
                        new Integer(ageCriterion), new Integer(ageRange)));
            } catch (Exception e) {
                e.printStackTrace();
                throw new NumberFormatException("advancesearch.error.age");
            }
        }

        // filter using relative name
        String relativeNameCriterion = request.getParameter("relativeName");
        if (!StringUtils.isBlank(relativeNameCriterion)) {
            filteredPatients = select(filteredPatients,
                    new RelativeNameMatcher(relativeNameCriterion));
        }

        // filter using date of visit
        String dateCriterion = request.getParameter("date");
        if (!StringUtils.isBlank(dateCriterion)) {
            try {
                String dateRange = request.getParameter("dateRange");
                filteredPatients = select(filteredPatients, new DateMatcher(
                        dateCriterion, new Integer(dateRange)));
            } catch (Exception e) {
                e.printStackTrace();
                throw new NumberFormatException("advancesearch.error.date");
            }
        }

        return filteredPatients;
    }

    // paging
    private List<Patient> pagePatient(List<Patient> patients, int currentPage,
                                      int pageSize) {
        int firstIndex = pageSize * currentPage;
        List<Patient> page = new ArrayList<Patient>();
        for (int i = firstIndex; i < (currentPage + 1) * pageSize; i++) {
            if (i < patients.size()) {
                page.add(patients.get(i));
            }
        }
        return page;
    }

    private List<PatientWrappers> patientsWithLastVisit(List<Patient> patients) {
        HospitalCoreService hcs = Context.getService(HospitalCoreService.class);
        List<PatientWrappers> wrappers = new ArrayList<PatientWrappers>();
        for (Patient patient : patients) {
            PatientWrappers pw = new PatientWrappers(patient, hcs.getLastVisitTime(patient));
            pw.setVoided(patient.getVoided());
            wrappers.add(pw);
        }
        return wrappers;
    }


    private List<Patient> select(List<Patient> patients, Matcher matcher) {
        List<Patient> result = new ArrayList<Patient>();
        for (Patient patient : patients) {
            if (matcher.matches(patient)) {
                result.add(patient);
            }
        }
        return result;
    }

    private Integer getInt(String value) {
        try {
            Integer number = Integer.parseInt(value);
            return number;
        } catch (Exception e) {
            return 0;
        }
    }
}
