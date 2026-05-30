package org.example.medical_record.service.impl;

import org.example.medical_record.config.ModelMapperConfig;
import org.example.medical_record.data.entity.*;
import org.example.medical_record.data.repository.*;
import org.example.medical_record.dto.ExaminationDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(SpringExtension.class)
class ExaminationServiceImplTest {

    @Mock ExaminationRepository examinationRepository;
    @Mock DoctorRepository      doctorRepository;
    @Mock PatientRepository     patientRepository;
    @Mock DiagnosisRepository   diagnosisRepository;
    @Mock ModelMapperConfig     modelMapperConfig;

    @InjectMocks ExaminationServiceImpl examinationService;

    @Test
    void getExaminationById_throwsWhenNotFound() {
        Mockito.when(examinationRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(NoSuchElementException.class,
                () -> examinationService.getExaminationById(99L));
    }

    @Test
    void getPatientHistory_returnsExaminationsForPatient() {
        Doctor  doc = Doctor.builder().id(1L).name("Д-р Тест").specialty("GP").build();
        Patient pat = Patient.builder().id(1L).name("Пациент").egn("0000000001")
                .insured(false).gpDoctor(doc).build();
        Diagnosis diag = Diagnosis.builder().id(1L).name("Грип").build();

        Examination e = Examination.builder()
                .id(1L).date(LocalDate.now()).price(BigDecimal.TEN)
                .doctor(doc).patient(pat).diagnosis(diag).build();

        Mockito.when(examinationRepository.findPatientHistory(1L)).thenReturn(List.of(e));
        Mockito.when(modelMapperConfig.modelMapper())
                .thenReturn(new org.modelmapper.ModelMapper());

        List<ExaminationDto> result = examinationService.getPatientHistory(1L);
        assertEquals(1, result.size());
    }

    @Test
    void deleteExamination_throwsWhenNotFound() {
        Mockito.when(examinationRepository.existsById(42L)).thenReturn(false);
        assertThrows(NoSuchElementException.class,
                () -> examinationService.deleteExamination(42L));
    }
}