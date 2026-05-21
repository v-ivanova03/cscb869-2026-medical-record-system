package org.example.medical_record;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

// @SpringBootTest зарежда целия Spring контекст
// @ActiveProfiles("test") казва на Spring да ползва application-test.properties
// (H2 вместо MySQL) — иначе тестът ще се опита да се свърже с реалната БД
@SpringBootTest
@ActiveProfiles("test")
class MedicalRecordApplicationTests {

    // Проверява само че Spring контекстът се зарежда без грешки
    // Ако имаш грешка в конфигурацията, тя ще гръмне тук
    @Test
    void contextLoads() {
    }
}