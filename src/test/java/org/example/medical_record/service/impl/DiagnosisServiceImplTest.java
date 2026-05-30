package org.example.medical_record.service.impl;

import org.example.medical_record.config.ModelMapperConfig;
import org.example.medical_record.data.entity.Diagnosis;
import org.example.medical_record.data.repository.DiagnosisRepository;
import org.example.medical_record.dto.DiagnosisDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
class DiagnosisServiceImplTest {

    @Mock DiagnosisRepository  diagnosisRepository;
    @Mock ModelMapperConfig    modelMapperConfig;
    @InjectMocks DiagnosisServiceImpl diagnosisService;

    @Test
    void getMostCommonDiagnosis_returnsEmptyWhenNoDiagnoses() {
        Mockito.when(diagnosisRepository.findMostCommonDiagnosis())
                .thenReturn(Optional.empty());

        // Вече не хвърля exception — връща Optional.empty()
        assertTrue(diagnosisService.getMostCommonDiagnosis().isEmpty());
    }

    @Test
    void getMostCommonDiagnosis_returnsMappedDto() {
        Diagnosis flu = Diagnosis.builder().id(1L).name("Грип").build();

        Mockito.when(diagnosisRepository.findMostCommonDiagnosis())
                .thenReturn(Optional.of(flu));
        Mockito.when(modelMapperConfig.modelMapper())
                .thenReturn(new ModelMapper());

        Optional<DiagnosisDto> result = diagnosisService.getMostCommonDiagnosis();

        assertTrue(result.isPresent());
        assertEquals("Грип", result.get().getName());
    }

    @Test
    void deleteDiagnosis_throwsWhenNotFound() {
        Mockito.when(diagnosisRepository.existsById(99L)).thenReturn(false);
        assertThrows(NoSuchElementException.class,
                () -> diagnosisService.deleteDiagnosis(99L));
    }

    @Test
    void getDiagnosisById_throwsWhenNotFound() {
        Mockito.when(diagnosisRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(NoSuchElementException.class,
                () -> diagnosisService.getDiagnosisById(99L));
    }
}