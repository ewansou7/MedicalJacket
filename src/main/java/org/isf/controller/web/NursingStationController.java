package org.isf.controller.web;

import org.isf.dao.Patient;
import org.isf.dao.PatientMeasurements;
import org.isf.dao.User;
import org.isf.models.ExaminationsModel;
import org.isf.repository.UserRepository;
import org.isf.service.ExaminationService;
import org.isf.service.PatientMeasurementsService;
import org.isf.service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletContext;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

@RequestMapping(value = "/nursing_station")

@Controller
public class NursingStationController {

    @Autowired
    protected ServletContext mContext;

    @Autowired
    protected PatientService patientService;

    @Autowired
    protected UserRepository userRepository;

    @Autowired
    protected ExaminationService examinationService;

    @Autowired
    protected PatientMeasurementsService patientMeasurementsService;

    @GetMapping(value = "/list")
    public ModelAndView getPatients(Model model) throws IOException, ParseException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByUserName(auth.getName());

        List<Patient> patients = patientMeasurementsService.getPatientsWithLastMeasurements();
        List<PatientMeasurements> patientMeasurements = new ArrayList<>();

        for (Patient patient : patients) {
            PatientMeasurements patientMeasurement = patientMeasurementsService.getLastByPatient(patient);
            patientMeasurements.add(patientMeasurement);
        }

        List<ExaminationsModel> measurementsInfo = new ArrayList<>();

        for (PatientMeasurements pM : patientMeasurements) {
            ExaminationsModel examinations = new ExaminationsModel(pM);
            examinations = examinationService.setExaminationColors(examinations, Integer.parseInt(examinations.getPatientAge()));
            measurementsInfo.add(examinations);
        }

        ModelAndView mv = new ModelAndView();
        mv.addObject("userName", "Welcome " + user.getUserName());
        mv.addObject("measurements", measurementsInfo);
        mv.setViewName("nursing_station");
        return mv;
    }

    @GetMapping("/get_measurements")
    public ModelAndView updateData(Model model) throws IOException, ParseException {
        List<Patient> patients = patientMeasurementsService.getPatientsWithLastMeasurements();
        List<PatientMeasurements> patientMeasurements = new ArrayList<>();

        for (Patient patient : patients) {
            PatientMeasurements patientMeasurement = patientMeasurementsService.getLastByPatient(patient);
            patientMeasurements.add(patientMeasurement);
        }

        List<ExaminationsModel> measurementsInfo = new ArrayList<>();

        for (PatientMeasurements pM : patientMeasurements) {
            ExaminationsModel examinations = new ExaminationsModel(pM);
            examinations = examinationService.setExaminationColors(examinations, Integer.parseInt(examinations.getPatientAge()));
            measurementsInfo.add(examinations);
        }

        ModelAndView mv= new ModelAndView("nursing_station :: #measurements");
        mv.addObject("measurements", measurementsInfo);

        return mv;
    }

}
