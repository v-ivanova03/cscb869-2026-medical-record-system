package org.example.medical_record.controller;

import lombok.RequiredArgsConstructor;
import org.example.medical_record.service.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

// Централна страница която събира ВСИЧКИТЕ 11 справки на едно място
// Достъпна само за ADMIN
@Controller
@RequestMapping("/stats")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class StatsMvcController {

    private final DoctorService    doctorService;
    private final PatientService   patientService;
    private final DiagnosisService diagnosisService;
    private final SickLeaveService sickLeaveService;

    @GetMapping
    public String allStats(Model model) {

        // --- Справки за лекари ---
        model.addAttribute("doctorsByPatients",      doctorService.getDoctorsWithPatientCount());
        model.addAttribute("doctorsByExaminations",  doctorService.getDoctorsWithExaminationCount());
        model.addAttribute("doctorsWithMostSickLeaves", doctorService.getDoctorsWithMostSickLeaves());

        // --- Справки за пациенти ---
        model.addAttribute("totalUninsuredPrice",    patientService.getTotalPriceForUninsuredPatients());
        model.addAttribute("priceByDoctor",          patientService.getPricePerDoctor());

        // --- Справки за диагнози ---
        model.addAttribute("mostCommonDiagnosis",    diagnosisService.getMostCommonDiagnosis());
        model.addAttribute("diagnosesWithCount",     diagnosisService.getAllWithCount());

        // --- Справки за болнични ---
        model.addAttribute("busiestMonth",           sickLeaveService.getMonthWithMostSickLeaves());

        return "stats/index";   // → templates/stats/index.html
    }
}
