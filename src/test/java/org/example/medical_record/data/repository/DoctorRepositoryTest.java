package org.example.medical_record.data.repository;

import org.example.medical_record.data.entity.Doctor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

// @DataJpaTest зарежда САМО JPA слоя (Repository + Entity)
// Ползва H2 in-memory БД автоматично (без MySQL)
// Всеки тест се изпълнява в транзакция и се rollback-ва накрая
@DataJpaTest
class DoctorRepositoryTest {

    @Autowired
    DoctorRepository doctorRepository;

    @Test
    void findByIsGpTrue_returnsOnlyGpDoctors() {
        // Arrange — подготвяме данните
        Doctor gp = Doctor.builder()
                .name("Д-р Иванов")
                .specialty("Обща медицина")
                .isGp(true)
                .build();
        Doctor specialist = Doctor.builder()
                .name("Д-р Петрова")
                .specialty("Кардиология")
                .isGp(false)
                .build();
        doctorRepository.saveAll(List.of(gp, specialist));

        // Act — извикваме метода
        List<Doctor> gpDoctors = doctorRepository.findByIsGpTrue();

        // Assert — проверяваме резултата
        assertEquals(1, gpDoctors.size());
        assertEquals("Д-р Иванов", gpDoctors.get(0).getName());
        assertTrue(gpDoctors.get(0).isGp());
    }

    @Test
    void findBySpecialty_returnsMatchingDoctors() {
        Doctor d1 = Doctor.builder().name("Д-р А").specialty("Кардиология").isGp(false).build();
        Doctor d2 = Doctor.builder().name("Д-р Б").specialty("Кардиология").isGp(false).build();
        Doctor d3 = Doctor.builder().name("Д-р В").specialty("Неврология").isGp(false).build();
        doctorRepository.saveAll(List.of(d1, d2, d3));

        List<Doctor> cardiologists = doctorRepository.findBySpecialty("Кардиология");

        assertEquals(2, cardiologists.size());
    }
}