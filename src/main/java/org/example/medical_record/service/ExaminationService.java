package org.example.medical_record.service;

import org.example.medical_record.dto.ExaminationDto;

import java.time.LocalDate;
import java.util.List;

public interface ExaminationService {

    List<ExaminationDto> getAllExaminations();
    ExaminationDto getExaminationById(Long id);
    ExaminationDto createExamination(ExaminationDto dto);
    ExaminationDto updateExamination(Long id, ExaminationDto dto);
    void deleteExamination(Long id);

    // Справки
    List<ExaminationDto> getPatientHistory(Long patientId);
    List<ExaminationDto> getByDoctorAndPeriod(Long doctorId, LocalDate from, LocalDate to);
}
