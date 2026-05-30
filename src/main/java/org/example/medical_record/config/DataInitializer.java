package org.example.medical_record.config;

import lombok.RequiredArgsConstructor;
import org.example.medical_record.data.entity.*;
import org.example.medical_record.data.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    @Bean
    CommandLineRunner seedDatabase(
            DoctorRepository doctorRepo,
            PatientRepository patientRepo,
            DiagnosisRepository diagnosisRepo,
            ExaminationRepository examinationRepo,
            SickLeaveRepository sickLeaveRepo,
            AppUserRepository appUserRepository,
            PasswordEncoder passwordEncoder
    ) {
        return args -> {

            if (appUserRepository.findByUsername("admin").isEmpty()) {
                appUserRepository.save(AppUser.builder()
                        .username("admin")
                        .password(passwordEncoder.encode("admin123"))
                        .role("ROLE_ADMIN")
                        .build());
            }

            if (doctorRepo.count() > 0) {
                return;
            }

            Doctor gp1 = doctorRepo.save(Doctor.builder()
                    .name("Д-р Иван Иванов")
                    .specialty("Обща медицина")
                    .isGp(true)
                    .build());

            Doctor gp2 = doctorRepo.save(Doctor.builder()
                    .name("Д-р Мария Петрова")
                    .specialty("Обща медицина")
                    .isGp(true)
                    .build());

            Doctor specialist1 = doctorRepo.save(Doctor.builder()
                    .name("Д-р Георги Георгиев")
                    .specialty("Кардиология")
                    .isGp(false)
                    .build());

            Doctor specialist2 = doctorRepo.save(Doctor.builder()
                    .name("Д-р Елена Стоянова")
                    .specialty("Неврология")
                    .isGp(false)
                    .build());

            Doctor specialist3 = doctorRepo.save(Doctor.builder()
                    .name("Д-р Петър Димитров")
                    .specialty("Ортопедия")
                    .isGp(false)
                    .build());

            Patient p1 = patientRepo.save(Patient.builder()
                    .name("Стефан Стефанов")
                    .egn("8501011234")
                    .insured(true)
                    .gpDoctor(gp1)
                    .build());

            Patient p2 = patientRepo.save(Patient.builder()
                    .name("Анна Николова")
                    .egn("9203025678")
                    .insured(false)
                    .gpDoctor(gp1)
                    .build());

            Patient p3 = patientRepo.save(Patient.builder()
                    .name("Тодор Тодоров")
                    .egn("7807139012")
                    .insured(true)
                    .gpDoctor(gp2)
                    .build());

            Patient p4 = patientRepo.save(Patient.builder()
                    .name("Виктория Василева")
                    .egn("0005173456")
                    .insured(false)
                    .gpDoctor(gp2)
                    .build());

            Patient p5 = patientRepo.save(Patient.builder()
                    .name("Кирил Кирилов")
                    .egn("8811227890")
                    .insured(true)
                    .gpDoctor(gp1)
                    .build());

            Diagnosis flu = diagnosisRepo.save(Diagnosis.builder()
                    .name("Грип")
                    .description("Остро вирусно заболяване на дихателните пътища")
                    .build());

            Diagnosis hypertension = diagnosisRepo.save(Diagnosis.builder()
                    .name("Хипертония")
                    .description("Повишено артериално налягане")
                    .build());

            Diagnosis diabetes = diagnosisRepo.save(Diagnosis.builder()
                    .name("Диабет тип 2")
                    .description("Нарушен глюкозен метаболизъм")
                    .build());

            Diagnosis angina = diagnosisRepo.save(Diagnosis.builder()
                    .name("Ангина")
                    .description("Остро възпаление на сливиците")
                    .build());

            Diagnosis migraine = diagnosisRepo.save(Diagnosis.builder()
                    .name("Мигрена")
                    .description("Повтарящо се главоболие")
                    .build());

            SickLeave sl1 = SickLeave.builder()
                    .startDate(LocalDate.of(2026, 1, 10))
                    .totalDays(7)
                    .build();

            SickLeave sl2 = SickLeave.builder()
                    .startDate(LocalDate.of(2026, 2, 15))
                    .totalDays(3)
                    .build();

            SickLeave sl3 = SickLeave.builder()
                    .startDate(LocalDate.of(2026, 2, 20))
                    .totalDays(5)
                    .build();

            examinationRepo.saveAll(List.of(
                    Examination.builder()
                            .date(LocalDate.of(2026, 1, 10))
                            .price(new BigDecimal("25.00"))
                            .treatment("Почивка, антивирусни")
                            .doctor(gp1)
                            .patient(p1)
                            .diagnosis(flu)
                            .sickLeave(sl1)
                            .build(),

                    Examination.builder()
                            .date(LocalDate.of(2026, 2, 5))
                            .price(new BigDecimal("40.00"))
                            .treatment("Понижаващи налягането медикаменти")
                            .doctor(specialist1)
                            .patient(p2)
                            .diagnosis(hypertension)
                            .build(),

                    Examination.builder()
                            .date(LocalDate.of(2026, 2, 15))
                            .price(new BigDecimal("30.00"))
                            .treatment("Диета, метформин")
                            .doctor(gp2)
                            .patient(p3)
                            .diagnosis(diabetes)
                            .sickLeave(sl2)
                            .build(),

                    Examination.builder()
                            .date(LocalDate.of(2026, 2, 20))
                            .price(new BigDecimal("20.00"))
                            .treatment("Антибиотик, гаргара")
                            .doctor(gp1)
                            .patient(p4)
                            .diagnosis(angina)
                            .sickLeave(sl3)
                            .build(),

                    Examination.builder()
                            .date(LocalDate.of(2026, 3, 1))
                            .price(new BigDecimal("60.00"))
                            .treatment("Триптани, почивка в тъмно")
                            .doctor(specialist2)
                            .patient(p5)
                            .diagnosis(migraine)
                            .build(),

                    Examination.builder()
                            .date(LocalDate.of(2026, 3, 12))
                            .price(new BigDecimal("25.00"))
                            .treatment("Почивка, антивирусни")
                            .doctor(gp2)
                            .patient(p1)
                            .diagnosis(flu)
                            .build(),

                    Examination.builder()
                            .date(LocalDate.of(2026, 4, 3))
                            .price(new BigDecimal("40.00"))
                            .treatment("Медикаментозна терапия")
                            .doctor(specialist1)
                            .patient(p3)
                            .diagnosis(hypertension)
                            .build()
            ));

            if (appUserRepository.findByUsername("doctor1").isEmpty()) {
                appUserRepository.save(AppUser.builder()
                        .username("doctor1")
                        .password(passwordEncoder.encode("doctor123"))
                        .role("ROLE_DOCTOR")
                        .doctor(gp1)
                        .build());
            }

            if (appUserRepository.findByUsername("patient1").isEmpty()) {
                appUserRepository.save(AppUser.builder()
                        .username("patient1")
                        .password(passwordEncoder.encode("patient123"))
                        .role("ROLE_PATIENT")
                        .patient(p1)
                        .build());
            }

            System.out.println("=== DataInitializer: начални данни и потребители заредени успешно ===");
        };
    }
}