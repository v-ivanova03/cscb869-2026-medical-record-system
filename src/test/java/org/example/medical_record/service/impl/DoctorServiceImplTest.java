package org.example.medical_record.service.impl;

import org.example.medical_record.config.ModelMapperConfig;
import org.example.medical_record.data.entity.Doctor;
import org.example.medical_record.data.repository.DoctorRepository;
import org.example.medical_record.dto.DoctorDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

// @ExtendWith(SpringExtension.class) — интегрира Mockito с JUnit 5
// Не зарежда Spring контекста → много по-бързо от @SpringBootTest
@ExtendWith(SpringExtension.class)
class DoctorServiceImplTest {

    // @Mock — създава фиктивен (mock) обект
    // Не вика реалната БД — ние казваме какво да "връща"
    @Mock
    private DoctorRepository doctorRepository;

    @Mock
    private ModelMapperConfig modelMapperConfig;

    // @InjectMocks — създава реален DoctorServiceImpl
    // и инжектира mock-овете в него
    @InjectMocks
    private DoctorServiceImpl doctorService;

    @Test
    void getAllDoctors_returnsMappedDtos() {
        Doctor d1 = Doctor.builder().id(1L).name("Д-р Иванов").specialty("Хирургия").build();
        Doctor d2 = Doctor.builder().id(2L).name("Д-р Петрова").specialty("Кардиология").build();

        // when(mock.метод()).thenReturn(стойност) — казваме какво да върне mock-а
        Mockito.when(doctorRepository.findAll()).thenReturn(List.of(d1, d2));
        Mockito.when(modelMapperConfig.mapList(any(), any()))
                .thenReturn(List.of(new DoctorDto(), new DoctorDto()));

        List<DoctorDto> result = doctorService.getAllDoctors();

        assertEquals(2, result.size());
    }

    @Test
    void getDoctorById_throwsWhenNotFound() {
        Mockito.when(doctorRepository.findById(99L)).thenReturn(Optional.empty());

        // assertThrows проверява, че методът ХВЪРЛЯ точно това exception
        assertThrows(NoSuchElementException.class,
                () -> doctorService.getDoctorById(99L));
    }
}