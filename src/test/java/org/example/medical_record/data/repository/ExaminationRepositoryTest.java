package org.example.medical_record.data.repository;

import org.example.medical_record.data.entity.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ExaminationRepositoryTest {

    @Autowired ExaminationRepository examinationRepository;
    @Autowired DoctorRepository      doctorRepository;
    @Autowired PatientRepository     patientRepository;
    @Autowired DiagnosisRepository   diagnosisRepository;

    private Doctor    doctor;
    private Patient   patient;
    private Diagnosis flu;

    @BeforeEach
    void setUp() {
        doctor  = doctorRepository.save(Doctor.builder()
                .name("Д-р Тест").specialty("Обща медицина").isGp(true).build());
        patient = patientRepository.save(Patient.builder()
                .name("Тест Пациент").egn("9000009999").insured(false).gpDoctor(doctor).build());
        flu     = diagnosisRepository.save(Diagnosis.builder()
                .name("Грип").description("Вирусно").build());
    }

    @Test
    void findByPatientId_returnsAllExaminationsForPatient() {
        examinationRepository.save(Examination.builder()
                .date(LocalDate.of(2026, 1, 5)).price(BigDecimal.TEN)
                .doctor(doctor).patient(patient).diagnosis(flu).build());
        examinationRepository.save(Examination.builder()
                .date(LocalDate.of(2026, 2, 10)).price(BigDecimal.TEN)
                .doctor(doctor).patient(patient).diagnosis(flu).build());

        List<Examination> result = examinationRepository.findByPatientId(patient.getId());
        assertEquals(2, result.size());
    }

    @Test
    void findByDoctorAndPeriod_filtersByDateRange() {
        examinationRepository.save(Examination.builder()
                .date(LocalDate.of(2026, 1, 5)).price(BigDecimal.TEN)
                .doctor(doctor).patient(patient).diagnosis(flu).build());
        examinationRepository.save(Examination.builder()
                .date(LocalDate.of(2026, 3, 20)).price(BigDecimal.TEN)
                .doctor(doctor).patient(patient).diagnosis(flu).build());

        // Търсим само в януари 2026
        List<Examination> result = examinationRepository.findByDoctorAndPeriod(
                null,
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2026, 1, 31)
        );
        assertEquals(1, result.size());
        assertEquals(LocalDate.of(2026, 1, 5), result.get(0).getDate());
    }

    @Test
    void findByDoctorAndPeriod_withDoctorFilter() {
        Doctor anotherDoctor = doctorRepository.save(Doctor.builder()
                .name("Д-р Друг").specialty("Кардиология").isGp(false).build());

        examinationRepository.save(Examination.builder()
                .date(LocalDate.of(2026, 2, 1)).price(BigDecimal.TEN)
                .doctor(doctor).patient(patient).diagnosis(flu).build());
        examinationRepository.save(Examination.builder()
                .date(LocalDate.of(2026, 2, 5)).price(BigDecimal.TEN)
                .doctor(anotherDoctor).patient(patient).diagnosis(flu).build());

        List<Examination> result = examinationRepository.findByDoctorAndPeriod(
                doctor.getId(), null, null);
        assertEquals(1, result.size());
    }
}