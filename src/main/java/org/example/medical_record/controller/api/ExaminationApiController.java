package org.example.medical_record.controller.api;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.medical_record.dto.ExaminationDto;
import org.example.medical_record.service.ExaminationService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/examinations")
@RequiredArgsConstructor
public class ExaminationApiController {

    private final ExaminationService examinationService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public ResponseEntity<List<ExaminationDto>> getAllExaminations() {
        return ResponseEntity.ok(examinationService.getAllExaminations());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'PATIENT')")
    public ResponseEntity<ExaminationDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(examinationService.getExaminationById(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public ResponseEntity<ExaminationDto> create(@Valid @RequestBody ExaminationDto dto) {
        return ResponseEntity.ok(examinationService.createExamination(dto));
    }

    // Само ADMIN и лекарят, направил прегледа, може да редактира
    // Допълнителната проверка "дали е ТОЙ лекарят" е в Service-а
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public ResponseEntity<ExaminationDto> update(
            @PathVariable Long id,
            @Valid @RequestBody ExaminationDto dto) {
        return ResponseEntity.ok(examinationService.updateExamination(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        examinationService.deleteExamination(id);
        return ResponseEntity.noContent().build();
    }

    // GET /api/examinations/patient/{patientId}/history
    @GetMapping("/patient/{patientId}/history")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'PATIENT')")
    public ResponseEntity<List<ExaminationDto>> getPatientHistory(
            @PathVariable Long patientId) {
        return ResponseEntity.ok(examinationService.getPatientHistory(patientId));
    }

    // GET /api/examinations/filter?doctorId=1&from=2026-01-01&to=2026-05-31
    // @RequestParam(required = false) → параметърът е незадължителен
    // @DateTimeFormat → казва на Spring как да парсва датата от URL-а
    @GetMapping("/filter")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public ResponseEntity<List<ExaminationDto>> getByDoctorAndPeriod(
            @RequestParam(required = false) Long doctorId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return ResponseEntity.ok(examinationService.getByDoctorAndPeriod(doctorId, from, to));
    }
}
