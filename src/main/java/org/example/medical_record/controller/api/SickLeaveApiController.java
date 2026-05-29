package org.example.medical_record.controller.api;

import lombok.RequiredArgsConstructor;
import org.example.medical_record.dto.SickLeaveDto;
import org.example.medical_record.service.SickLeaveService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

// Болничните се СЪЗДАВАТ само като част от преглед (в ExaminationApiController)
// Тук имаме само четене, изтриване и справки
@RestController
@RequestMapping("/api/sick-leaves")
@RequiredArgsConstructor
public class SickLeaveApiController {

    private final SickLeaveService sickLeaveService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public ResponseEntity<List<SickLeaveDto>> getAll() {
        return ResponseEntity.ok(sickLeaveService.getAllSickLeaves());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'PATIENT')")
    public ResponseEntity<SickLeaveDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(sickLeaveService.getSickLeaveById(id));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        sickLeaveService.deleteSickLeave(id);
        return ResponseEntity.noContent().build();
    }

    // GET /api/sick-leaves/stats/busiest-month → месец с най-много болнични
    @GetMapping("/stats/busiest-month")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public ResponseEntity<Map<String, Object>> getBusiestMonth() {
        return ResponseEntity.ok(sickLeaveService.getMonthWithMostSickLeaves());
    }
}