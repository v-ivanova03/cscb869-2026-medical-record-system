package org.example.medical_record.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.medical_record.dto.DiagnosisDto;
import org.example.medical_record.service.DiagnosisService;
import org.example.medical_record.service.PatientService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/diagnoses")
@RequiredArgsConstructor
public class DiagnosisMvcController {

    private final DiagnosisService diagnosisService;
    private final PatientService   patientService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'PATIENT')")
    public String list(Model model) {
        model.addAttribute("diagnoses", diagnosisService.getAllWithCount());
        return "diagnoses/list";
    }

    @GetMapping("/create")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public String createForm(Model model) {
        model.addAttribute("diagnosis", new DiagnosisDto());
        return "diagnoses/form";
    }

    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public String create(@Valid @ModelAttribute("diagnosis") DiagnosisDto dto,
                         BindingResult result) {
        if (result.hasErrors()) return "diagnoses/form";
        diagnosisService.createDiagnosis(dto);
        return "redirect:/diagnoses";
    }

    @GetMapping("/{id}/edit")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("diagnosis", diagnosisService.getDiagnosisById(id));
        return "diagnoses/form";
    }

    @PostMapping("/{id}/edit")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public String edit(@PathVariable Long id,
                       @Valid @ModelAttribute("diagnosis") DiagnosisDto dto,
                       BindingResult result) {
        if (result.hasErrors()) return "diagnoses/form";
        diagnosisService.updateDiagnosis(id, dto);
        return "redirect:/diagnoses";
    }

    @PostMapping("/{id}/delete")
    @PreAuthorize("hasRole('ADMIN')")
    public String delete(@PathVariable Long id) {
        diagnosisService.deleteDiagnosis(id);
        return "redirect:/diagnoses";
    }

    // Пациенти с дадена диагноза
    @GetMapping("/{id}/patients")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public String patientsByDiagnosis(@PathVariable Long id, Model model) {
        DiagnosisDto diagnosis = diagnosisService.getDiagnosisById(id);
        model.addAttribute("diagnosis", diagnosis);
        model.addAttribute("patients",  patientService.getPatientsByDiagnosis(diagnosis.getName()));
        return "diagnoses/patients";
    }
}