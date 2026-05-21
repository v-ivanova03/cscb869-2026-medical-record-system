package org.example.medical_record.controller.api;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.medical_record.dto.ExaminationDto;
import org.example.medical_record.dto.PatientDto;
import org.example.medical_record.service.ExaminationService;
import org.example.medical_record.service.PatientService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/patients")
@RequiredArgsConstructor
public class PatientApiController {

    private final PatientService     patientService;
    private final ExaminationService examinationService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public ResponseEntity<List<PatientDto>> getAllPatients() {
        return ResponseEntity.ok(patientService.getAllPatients());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'PATIENT')")
    public ResponseEntity<PatientDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(patientService.getPatientById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PatientDto> create(@Valid @RequestBody PatientDto dto) {
        return ResponseEntity.ok(patientService.createPatient(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PatientDto> update(@PathVariable Long id,
                                             @Valid @RequestBody PatientDto dto) {
        return ResponseEntity.ok(patientService.updatePatient(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        patientService.deletePatient(id);
        return ResponseEntity.noContent().build();
    }

    // GET /api/patients/{id}/history → история на посещенията на пациент
    @GetMapping("/{id}/history")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'PATIENT')")
    public ResponseEntity<List<ExaminationDto>> getHistory(@PathVariable Long id) {
        return ResponseEntity.ok(examinationService.getPatientHistory(id));
    }

    // Справки
    @GetMapping("/by-diagnosis")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public ResponseEntity<List<PatientDto>> getByDiagnosis(@RequestParam String diagnosisName) {
        return ResponseEntity.ok(patientService.getPatientsByDiagnosis(diagnosisName));
    }

    @GetMapping("/by-gp/{gpDoctorId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public ResponseEntity<List<PatientDto>> getByGp(@PathVariable Long gpDoctorId) {
        return ResponseEntity.ok(patientService.getPatientsByGpDoctor(gpDoctorId));
    }

    @GetMapping("/stats/uninsured-total")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Double> getUninsuredTotal() {
        return ResponseEntity.ok(patientService.getTotalPriceForUninsuredPatients());
    }
}
