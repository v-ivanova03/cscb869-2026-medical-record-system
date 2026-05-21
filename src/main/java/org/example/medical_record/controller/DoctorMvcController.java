package org.example.medical_record.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.medical_record.dto.DoctorDto;
import org.example.medical_record.service.DoctorService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/doctors")
@RequiredArgsConstructor
public class DoctorMvcController {

    private final DoctorService doctorService;

    // GET /doctors → списък на всички лекари
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'PATIENT')")
    public String listDoctors(Model model) {
        model.addAttribute("doctors", doctorService.getAllDoctors());
        return "doctors/list";   // → templates/doctors/list.html
    }

    // GET /doctors/create → форма за нов лекар
    @GetMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    public String createForm(Model model) {
        model.addAttribute("doctor", new DoctorDto());
        return "doctors/form";   // → templates/doctors/form.html
    }

    // POST /doctors/create → запис на нов лекар
    // @Valid → валидира DoctorDto
    // BindingResult → държи грешките при валидация
    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    public String create(@Valid @ModelAttribute("doctor") DoctorDto dto,
                         BindingResult result) {
        if (result.hasErrors()) {
            // Има грешки → върни формата с грешките
            return "doctors/form";
        }
        doctorService.createDoctor(dto);
        return "redirect:/doctors";   // Пренасочи към списъка
    }

    // GET /doctors/{id}/edit → форма за редакция
    @GetMapping("/{id}/edit")
    @PreAuthorize("hasRole('ADMIN')")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("doctor", doctorService.getDoctorById(id));
        return "doctors/form";
    }

    // POST /doctors/{id}/edit → запис на редакцията
    @PostMapping("/{id}/edit")
    @PreAuthorize("hasRole('ADMIN')")
    public String edit(@PathVariable Long id,
                       @Valid @ModelAttribute("doctor") DoctorDto dto,
                       BindingResult result) {
        if (result.hasErrors()) return "doctors/form";
        doctorService.updateDoctor(id, dto);
        return "redirect:/doctors";
    }

    // POST /doctors/{id}/delete → изтриване
    @PostMapping("/{id}/delete")
    @PreAuthorize("hasRole('ADMIN')")
    public String delete(@PathVariable Long id) {
        doctorService.deleteDoctor(id);
        return "redirect:/doctors";
    }

    // GET /doctors/stats → справки за лекари
    @GetMapping("/stats")
    @PreAuthorize("hasRole('ADMIN')")
    public String stats(Model model) {
        model.addAttribute("byPatients",    doctorService.getDoctorsWithPatientCount());
        model.addAttribute("byExaminations", doctorService.getDoctorsWithExaminationCount());
        model.addAttribute("mostSickLeaves", doctorService.getDoctorsWithMostSickLeaves());
        return "doctors/stats";
    }
}
