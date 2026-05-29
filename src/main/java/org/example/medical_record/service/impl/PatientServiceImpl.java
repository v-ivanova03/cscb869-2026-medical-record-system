package org.example.medical_record.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.medical_record.config.ModelMapperConfig;
import org.example.medical_record.data.entity.Doctor;
import org.example.medical_record.data.entity.Patient;
import org.example.medical_record.data.repository.DoctorRepository;
import org.example.medical_record.data.repository.PatientRepository;
import org.example.medical_record.dto.DoctorDto;
import org.example.medical_record.dto.PatientDto;
import org.example.medical_record.service.PatientService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class PatientServiceImpl implements PatientService {

    private final PatientRepository patientRepository;
    private final DoctorRepository  doctorRepository;
    private final ModelMapperConfig modelMapperConfig;

    @Override
    public List<PatientDto> getAllPatients() {
        return modelMapperConfig.mapList(patientRepository.findAll(), PatientDto.class);
    }

    @Override
    public PatientDto getPatientById(Long id) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Пациент с id=" + id + " не съществува"));
        return toDto(patient);
    }

    @Override
    public PatientDto createPatient(PatientDto dto) {
        Patient patient = new Patient();
        patient.setName(dto.getName());
        patient.setEgn(dto.getEgn());
        patient.setInsured(dto.isInsured());

        if (dto.getGpDoctorId() != null) {
            Doctor gp = doctorRepository.findById(dto.getGpDoctorId())
                    .orElseThrow(() -> new NoSuchElementException("Лекар не съществува"));
            patient.setGpDoctor(gp);
        }

        Patient saved = patientRepository.save(patient);
        return toDto(saved);
    }

    @Override
    public PatientDto updatePatient(Long id, PatientDto dto) {
        Patient existing = patientRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Пациент с id=" + id + " не съществува"));
        existing.setName(dto.getName());
        existing.setEgn(dto.getEgn());
        existing.setInsured(dto.isInsured());
        if (dto.getGpDoctorId() != null) {
            Doctor gp = doctorRepository.findById(dto.getGpDoctorId())
                    .orElseThrow(() -> new NoSuchElementException("Лекар не съществува"));
            existing.setGpDoctor(gp);
        }
        return toDto(patientRepository.save(existing));
    }

    @Override
    public void deletePatient(Long id) {
        if (!patientRepository.existsById(id)) {
            throw new NoSuchElementException("Пациент с id=" + id + " не съществува");
        }
        patientRepository.deleteById(id);
    }

    @Override
    public List<PatientDto> getPatientsByDiagnosis(String diagnosisName) {
        return modelMapperConfig.mapList(
                patientRepository.findPatientsByDiagnosisName(diagnosisName), PatientDto.class);
    }

    @Override
    public List<PatientDto> getPatientsByGpDoctor(Long gpDoctorId) {
        return modelMapperConfig.mapList(
                patientRepository.findByGpDoctorId(gpDoctorId), PatientDto.class);
    }

    @Override
    public Double getTotalPriceForUninsuredPatients() {
        Double total = patientRepository.getTotalPriceForUninsuredPatients();
        return total != null ? total : 0.0;
    }

    // Стойност платена от неосигурени пациенти, разбита по лекар
    // Ползваме examinationCount в DoctorDto за да пазим сумата
    @Override
    public List<DoctorDto> getPricePerDoctor() {
        return patientRepository.getTotalPricePerDoctorForUninsuredPatients().stream()
                .map(row -> {
                    Doctor doc = (Doctor) row[0];
                    Double sum = row[1] != null ? ((Number) row[1]).doubleValue() : 0.0;
                    DoctorDto dto = modelMapperConfig.modelMapper().map(doc, DoctorDto.class);
                    dto.setTotalRevenue(sum);
                    return dto;
                })
                .toList();
    }

    private PatientDto toDto(Patient patient) {
        PatientDto dto = modelMapperConfig.modelMapper().map(patient, PatientDto.class);
        if (patient.getGpDoctor() != null) {
            dto.setGpDoctorId(patient.getGpDoctor().getId());
            dto.setGpDoctorName(patient.getGpDoctor().getName());
        }
        return dto;
    }
}
