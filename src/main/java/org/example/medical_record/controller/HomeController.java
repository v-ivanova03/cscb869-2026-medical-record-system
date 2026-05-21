package org.example.medical_record.controller;

import lombok.RequiredArgsConstructor;
import org.example.medical_record.service.DoctorService;
import org.example.medical_record.service.DiagnosisService;
import org.example.medical_record.service.SickLeaveService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

// @Controller (НЕ @RestController) → методите връщат имена на Thymeleaf шаблони
// Spring търси файла в src/main/resources/templates/{name}.html
@Controller
@RequiredArgsConstructor
public class HomeController {

    private final DoctorService    doctorService;
    private final DiagnosisService diagnosisService;
    private final SickLeaveService sickLeaveService;

    // GET / → начална страница
    @GetMapping("/")
    public String home(Model model) {
        // Model добавя данни, достъпни в Thymeleaf чрез ${...}
        model.addAttribute("doctorCount",    doctorService.getAllDoctors().size());
        model.addAttribute("busiestMonth",   sickLeaveService.getMonthWithMostSickLeaves());
        try {
            model.addAttribute("mostCommonDiag", diagnosisService.getMostCommonDiagnosis());
        } catch (Exception e) {
            model.addAttribute("mostCommonDiag", null); // Показва "Няма данни"
        }
        return "index";   // → templates/index.html
    }

    // GET /login → страница за вход (Spring Security я ползва)
    @GetMapping("/login")
    public String login() {
        return "login";   // → templates/login.html
    }
}
