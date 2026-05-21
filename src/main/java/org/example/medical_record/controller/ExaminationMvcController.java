package org.example.medical_record.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.medical_record.dto.ExaminationDto;
import org.example.medical_record.service.DiagnosisService;
import org.example.medical_record.service.DoctorService;
import org.example.medical_record.service.ExaminationService;
import org.example.medical_record.service.PatientService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Controller
@RequestMapping("/examinations")
@RequiredArgsConstructor
public class ExaminationMvcController {

    private final ExaminationService examinationService;
    private final DoctorService      doctorService;
    private final PatientService     patientService;
    private final DiagnosisService   diagnosisService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public String list(Model model) {
        model.addAttribute("examinations", examinationService.getAllExaminations());
        return "examinations/list";
    }

    @GetMapping("/create")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public String createForm(Model model) {
        model.addAttribute("examination", new ExaminationDto());
        model.addAttribute("doctors",     doctorService.getAllDoctors());
        model.addAttribute("patients",    patientService.getAllPatients());
        model.addAttribute("diagnoses",   diagnosisService.getAllDiagnoses());
        return "examinations/form";
    }

    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public String create(@Valid @ModelAttribute("examination") ExaminationDto dto,
                         BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("doctors",   doctorService.getAllDoctors());
            model.addAttribute("patients",  patientService.getAllPatients());
            model.addAttribute("diagnoses", diagnosisService.getAllDiagnoses());
            return "examinations/form";
        }
        examinationService.createExamination(dto);
        return "redirect:/examinations";
    }

    @GetMapping("/{id}/edit")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("examination", examinationService.getExaminationById(id));
        model.addAttribute("doctors",     doctorService.getAllDoctors());
        model.addAttribute("patients",    patientService.getAllPatients());
        model.addAttribute("diagnoses",   diagnosisService.getAllDiagnoses());
        return "examinations/form";
    }

    @PostMapping("/{id}/edit")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public String edit(@PathVariable Long id,
                       @Valid @ModelAttribute("examination") ExaminationDto dto,
                       BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("doctors",   doctorService.getAllDoctors());
            model.addAttribute("patients",  patientService.getAllPatients());
            model.addAttribute("diagnoses", diagnosisService.getAllDiagnoses());
            return "examinations/form";
        }
        examinationService.updateExamination(id, dto);
        return "redirect:/examinations";
    }

    @PostMapping("/{id}/delete")
    @PreAuthorize("hasRole('ADMIN')")
    public String delete(@PathVariable Long id) {
        examinationService.deleteExamination(id);
        return "redirect:/examinations";
    }

    // Филтриране по лекар и/или период
    @GetMapping("/filter")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public String filter(
            @RequestParam(required = false) Long doctorId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            Model model) {
        model.addAttribute("examinations", examinationService.getByDoctorAndPeriod(doctorId, from, to));
        model.addAttribute("doctors",      doctorService.getAllDoctors());
        model.addAttribute("selectedDoctor", doctorId);
        model.addAttribute("from",           from);
        model.addAttribute("to",             to);
        return "examinations/filter";
    }
}

