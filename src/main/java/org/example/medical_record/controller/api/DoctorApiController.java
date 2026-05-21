package org.example.medical_record.controller.api;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.medical_record.dto.DoctorDto;
import org.example.medical_record.service.DoctorService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// @RestController = @Controller + @ResponseBody
// Всеки метод връща JSON автоматично (не HTML)
// @RequestMapping("/api/doctors") = base URL за всички методи
@RestController
@RequestMapping("/api/doctors")
@RequiredArgsConstructor
public class DoctorApiController {

    private final DoctorService doctorService;

    // GET /api/doctors → всички лекари
    // @PreAuthorize — проверява РОЛЯТА преди да изпълни метода
    // hasAnyRole('ADMIN','DOCTOR') → само ADMIN и DOCTOR имат достъп
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'PATIENT')")
    public ResponseEntity<List<DoctorDto>> getAllDoctors() {
        return ResponseEntity.ok(doctorService.getAllDoctors());
    }

    // GET /api/doctors/{id} → конкретен лекар по id
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'PATIENT')")
    public ResponseEntity<DoctorDto> getDoctorById(@PathVariable Long id) {
        return ResponseEntity.ok(doctorService.getDoctorById(id));
    }

    // GET /api/doctors/gp → само GP лекари (за dropdown-и)
    @GetMapping("/gp")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'PATIENT')")
    public ResponseEntity<List<DoctorDto>> getGpDoctors() {
        return ResponseEntity.ok(doctorService.getGpDoctors());
    }

    // POST /api/doctors → създай нов лекар
    // @Valid → активира валидацията от DTO анотациите (@NotBlank и т.н.)
    // @RequestBody → Spring чете JSON от тялото на заявката и го конвертира към DTO
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DoctorDto> createDoctor(@Valid @RequestBody DoctorDto doctorDto) {
        return ResponseEntity.ok(doctorService.createDoctor(doctorDto));
    }

    // PUT /api/doctors/{id} → обнови лекар
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DoctorDto> updateDoctor(
            @PathVariable Long id,
            @Valid @RequestBody DoctorDto doctorDto) {
        return ResponseEntity.ok(doctorService.updateDoctor(id, doctorDto));
    }

    // DELETE /api/doctors/{id} → изтрий лекар
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteDoctor(@PathVariable Long id) {
        doctorService.deleteDoctor(id);
        // 204 No Content = успех, но нямаме какво да върнем
        return ResponseEntity.noContent().build();
    }

    // ------------------------------------------------------------------
    // Справки
    // ------------------------------------------------------------------

    // GET /api/doctors/stats/patients → брой пациенти при всеки GP
    @GetMapping("/stats/patients")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<DoctorDto>> getDoctorsWithPatientCount() {
        return ResponseEntity.ok(doctorService.getDoctorsWithPatientCount());
    }

    // GET /api/doctors/stats/examinations → брой прегледи при всеки лекар
    @GetMapping("/stats/examinations")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<DoctorDto>> getDoctorsWithExaminationCount() {
        return ResponseEntity.ok(doctorService.getDoctorsWithExaminationCount());
    }

    // GET /api/doctors/stats/sick-leaves → лекар(и) с най-много болнични
    @GetMapping("/stats/sick-leaves")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<DoctorDto>> getDoctorsWithMostSickLeaves() {
        return ResponseEntity.ok(doctorService.getDoctorsWithMostSickLeaves());
    }
}
