package org.example.medical_record.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.medical_record.dto.AppUserDto;
import org.example.medical_record.service.AppUserService;
import org.example.medical_record.service.DoctorService;
import org.example.medical_record.service.PatientService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/users")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class UserMvcController {

    // Ползваме Service интерфейси — не директно repository-та
    // Това следва същия pattern като всички останали контролери
    private final AppUserService appUserService;
    private final DoctorService  doctorService;
    private final PatientService patientService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("users", appUserService.getAllUsers());
        return "users/list";
    }

    @GetMapping("/create")
    public String createForm(Model model) {
        model.addAttribute("user",     new AppUserDto());
        model.addAttribute("doctors",  doctorService.getAllDoctors());
        model.addAttribute("patients", patientService.getAllPatients());
        return "users/form";
    }

    @PostMapping("/create")
    public String create(@Valid @ModelAttribute("user") AppUserDto dto,
                         BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("doctors",  doctorService.getAllDoctors());
            model.addAttribute("patients", patientService.getAllPatients());
            return "users/form";
        }
        try {
            appUserService.createUser(dto);
        } catch (IllegalArgumentException e) {
            // Username вече съществува
            result.rejectValue("username", "duplicate", e.getMessage());
            model.addAttribute("doctors",  doctorService.getAllDoctors());
            model.addAttribute("patients", patientService.getAllPatients());
            return "users/form";
        }
        return "redirect:/users";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("user",     appUserService.getUserById(id));
        model.addAttribute("doctors",  doctorService.getAllDoctors());
        model.addAttribute("patients", patientService.getAllPatients());
        return "users/form";
    }

    @PostMapping("/{id}/edit")
    public String edit(@PathVariable Long id,
                       @Valid @ModelAttribute("user") AppUserDto dto,
                       BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("doctors",  doctorService.getAllDoctors());
            model.addAttribute("patients", patientService.getAllPatients());
            return "users/form";
        }
        appUserService.updateUser(id, dto);
        return "redirect:/users";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        appUserService.deleteUser(id);
        return "redirect:/users";
    }
}
