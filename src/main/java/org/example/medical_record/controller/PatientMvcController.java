package org.example.medical_record.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.medical_record.dto.PatientDto;
import org.example.medical_record.service.DoctorService;
import org.example.medical_record.service.ExaminationService;
import org.example.medical_record.service.PatientService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/patients")
@RequiredArgsConstructor
public class PatientMvcController {

    private final PatientService     patientService;
    private final DoctorService      doctorService;
    private final ExaminationService examinationService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public String list(Model model) {
        model.addAttribute("patients", patientService.getAllPatients());
        return "patients/list";
    }

    @GetMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    public String createForm(Model model) {
        model.addAttribute("patient",   new PatientDto());
        // Подаваме само GP лекарите за dropdown-а
        model.addAttribute("gpDoctors", doctorService.getGpDoctors());
        return "patients/form";
    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    public String create(@Valid @ModelAttribute("patient") PatientDto dto,
                         BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("gpDoctors", doctorService.getGpDoctors());
            return "patients/form";
        }
        patientService.createPatient(dto);
        return "redirect:/patients";
    }

    @GetMapping("/{id}/edit")
    @PreAuthorize("hasRole('ADMIN')")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("patient",   patientService.getPatientById(id));
        model.addAttribute("gpDoctors", doctorService.getGpDoctors());
        return "patients/form";
    }

    @PostMapping("/{id}/edit")
    @PreAuthorize("hasRole('ADMIN')")
    public String edit(@PathVariable Long id,
                       @Valid @ModelAttribute("patient") PatientDto dto,
                       BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("gpDoctors", doctorService.getGpDoctors());
            return "patients/form";
        }
        patientService.updatePatient(id, dto);
        return "redirect:/patients";
    }

    @PostMapping("/{id}/delete")
    @PreAuthorize("hasRole('ADMIN')")
    public String delete(@PathVariable Long id) {
        patientService.deletePatient(id);
        return "redirect:/patients";
    }

    // История на посещенията на конкретен пациент
    @GetMapping("/{id}/history")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'PATIENT')")
    public String history(@PathVariable Long id, Model model) {
        model.addAttribute("patient",      patientService.getPatientById(id));
        model.addAttribute("examinations", examinationService.getPatientHistory(id));
        return "patients/history";
    }
}