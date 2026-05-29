package org.example.medical_record.service;

import org.example.medical_record.dto.DoctorDto;

import java.util.List;

// Интерфейсът дефинира КАКВО може Service-а да прави
// Имплементацията (DoctorServiceImpl) дефинира КАК го прави
// Това разделение позволява лесно mock-ване в тестове
public interface DoctorService {

    List<DoctorDto> getAllDoctors();

    DoctorDto getDoctorById(Long id);

    DoctorDto createDoctor(DoctorDto doctorDto);

    DoctorDto updateDoctor(Long id, DoctorDto doctorDto);

    void deleteDoctor(Long id);

    // Само GP лекарите (за dropdown при регистрация на пациент)
    List<DoctorDto> getGpDoctors();

    // Справки
    List<DoctorDto> getDoctorsWithPatientCount();
    List<DoctorDto> getDoctorsWithExaminationCount();
    List<DoctorDto> getDoctorsWithMostSickLeaves();
}
