package org.example.medical_record.service;

import org.example.medical_record.dto.DiagnosisDto;

import java.util.List;
import java.util.Optional;

public interface DiagnosisService {

    List<DiagnosisDto> getAllDiagnoses();
    DiagnosisDto getDiagnosisById(Long id);
    DiagnosisDto createDiagnosis(DiagnosisDto dto);
    DiagnosisDto updateDiagnosis(Long id, DiagnosisDto dto);
    void deleteDiagnosis(Long id);

    // Optional защото може да няма никакви прегледи/диагнози
    Optional<DiagnosisDto> getMostCommonDiagnosis();

    List<DiagnosisDto> getAllWithCount();
}