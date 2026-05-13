package org.example.medical_record;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// @SpringBootApplication = три анотации в една:
//   @Configuration       — този клас е конфигурационен
//   @EnableAutoConfiguration — Spring Boot сам конфигурира beans
//   @ComponentScan       — сканира пакета за @Component, @Service и т.н.
@SpringBootApplication
public class MedicalRecordApplication {

    public static void main(String[] args) {
        SpringApplication.run(MedicalRecordApplication.class, args);
    }
}