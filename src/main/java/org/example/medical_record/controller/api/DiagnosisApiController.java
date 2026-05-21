package org.example.medical_record.controller.api;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.medical_record.dto.DiagnosisDto;
import org.example.medical_record.service.DiagnosisService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/diagnoses")
@RequiredArgsConstructor
public class DiagnosisApiController {

    private final DiagnosisService diagnosisService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'PATIENT')")
    public ResponseEntity<List<DiagnosisDto>> getAll() {
        return ResponseEntity.ok(diagnosisService.getAllDiagnoses());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'PATIENT')")
    public ResponseEntity<DiagnosisDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(diagnosisService.getDiagnosisById(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public ResponseEntity<DiagnosisDto> create(@Valid @RequestBody DiagnosisDto dto) {
        return ResponseEntity.ok(diagnosisService.createDiagnosis(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public ResponseEntity<DiagnosisDto> update(@PathVariable Long id,
                                               @Valid @RequestBody DiagnosisDto dto) {
        return ResponseEntity.ok(diagnosisService.updateDiagnosis(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        diagnosisService.deleteDiagnosis(id);
        return ResponseEntity.noContent().build();
    }

    // GET /api/diagnoses/stats/most-common → най-честа диагноза
    @GetMapping("/stats/most-common")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public ResponseEntity<DiagnosisDto> getMostCommon() {

        return diagnosisService.getMostCommonDiagnosis()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // GET /api/diagnoses/stats/with-count → всички с брой употреби
    @GetMapping("/stats/with-count")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public ResponseEntity<List<DiagnosisDto>> getAllWithCount() {
        return ResponseEntity.ok(diagnosisService.getAllWithCount());
    }
}
