package org.example.medical_record.data.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "examinations")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Examination {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Датата е задължителна")
    @Column(nullable = false)
    private LocalDate date;

    // Цената на прегледа — определя се от лекаря
    // BigDecimal е по-точен от double за парични суми
    @PositiveOrZero
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    // Лечение (текстово поле — незадължително)
    @Column(columnDefinition = "TEXT")
    private String treatment;

    // -------------------------------------------------------
    // Връзки — Examination е "центърът" на модела
    // -------------------------------------------------------

    // Кой лекар е направил прегледа
    // nullable = false: всеки преглед ТРЯБВА да има лекар
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", nullable = false)
    @ToString.Exclude
    private Doctor doctor;

    // Кой пациент е прегледан
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    @ToString.Exclude
    private Patient patient;

    // Каква диагноза е поставена
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "diagnosis_id", nullable = false)
    @ToString.Exclude
    private Diagnosis diagnosis;

    // Болничен лист — незадължителен (не всеки преглед издава болничен)
    // CascadeType.ALL: ако изтрием преглед, болничният се изтрива автоматично
    // orphanRemoval: ако премахнем болничния от прегледа, изтрива се от БД
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "sick_leave_id", nullable = true)
    @ToString.Exclude
    private SickLeave sickLeave;
}