package org.example.medical_record.service;

import org.example.medical_record.dto.DoctorDto;
import org.example.medical_record.dto.PatientDto;

import java.util.List;

public interface PatientService {

    List<PatientDto> getAllPatients();
    PatientDto getPatientById(Long id);
    PatientDto createPatient(PatientDto patientDto);
    PatientDto updatePatient(Long id, PatientDto patientDto);
    void deletePatient(Long id);

    // Справки
    List<PatientDto> getPatientsByDiagnosis(String diagnosisName);
    List<PatientDto> getPatientsByGpDoctor(Long gpDoctorId);
    Double getTotalPriceForUninsuredPatients();
    // Стойност платена от неосигурени пациенти, разбита по лекар
    List<DoctorDto> getPricePerDoctor();
}
