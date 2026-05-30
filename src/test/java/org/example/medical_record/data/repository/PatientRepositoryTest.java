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
class PatientRepositoryTest {

    @Autowired PatientRepository     patientRepository;
    @Autowired DoctorRepository      doctorRepository;
    @Autowired DiagnosisRepository   diagnosisRepository;
    @Autowired ExaminationRepository examinationRepository;

    private Doctor    gp;
    private Diagnosis flu;
    private Patient   p1, p2;

    @BeforeEach
    void setUp() {
        gp  = doctorRepository.save(Doctor.builder()
                .name("Д-р Иванов").specialty("Обща медицина").isGp(true).build());
        flu = diagnosisRepository.save(Diagnosis.builder()
                .name("Грип").description("Вирусно").build());
        p1  = patientRepository.save(Patient.builder()
                .name("Иван").egn("8501011111").insured(true).gpDoctor(gp).build());
        p2  = patientRepository.save(Patient.builder()
                .name("Мария").egn("9203022222").insured(false).gpDoctor(gp).build());
    }

    @Test
    void findByGpDoctorId_returnsCorrectPatients() {
        List<Patient> result = patientRepository.findByGpDoctorId(gp.getId());
        assertEquals(2, result.size());
    }

    @Test
    void findByEgn_returnsCorrectPatient() {
        var result = patientRepository.findByEgn("9203022222");
        assertTrue(result.isPresent());
        assertEquals("Мария", result.get().getName());
    }

    @Test
    void findPatientsByDiagnosisName_returnsOnlyMatching() {
        examinationRepository.save(Examination.builder()
                .date(LocalDate.now()).price(BigDecimal.TEN)
                .doctor(gp).patient(p1).diagnosis(flu).build());
        examinationRepository.save(Examination.builder()
                .date(LocalDate.now()).price(BigDecimal.TEN)
                .doctor(gp).patient(p2).diagnosis(flu).build());

        List<Patient> result = patientRepository.findPatientsByDiagnosisName("Грип");
        assertEquals(2, result.size());
    }

    @Test
    void getTotalPriceForUninsuredPatients_sumsOnlyUninsured() {
        // p2 е НЕосигурен → влиза в сумата
        examinationRepository.save(Examination.builder()
                .date(LocalDate.now()).price(new BigDecimal("30.00"))
                .doctor(gp).patient(p2).diagnosis(flu).build());
        // p1 е осигурен → НЕ влиза
        examinationRepository.save(Examination.builder()
                .date(LocalDate.now()).price(new BigDecimal("50.00"))
                .doctor(gp).patient(p1).diagnosis(flu).build());

        Double total = patientRepository.getTotalPriceForUninsuredPatients();
        assertEquals(30.0, total);
    }
}
