package org.example.medical_record.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExaminationDto {

    private Long id;

    @NotNull(message = "Датата е задължителна")
    private LocalDate date;

    @PositiveOrZero
    private BigDecimal price;

    private String treatment;

    // Референции към свързаните обекти — само id + display name
    // (не вгнездваме цели DTO-та)
    @NotNull(message = "Изберете лекар")
    private Long doctorId;    // null = веднага ясна грешка
    private String doctorName;

    @NotNull(message = "Изберете пациент")
    private Long patientId;

    private String patientName;

    @NotNull(message = "Изберете диагноза")
    private Long diagnosisId;
    private String diagnosisName;

    private SickLeaveDto sickLeave;

    // Дали пациентът е осигурен — ползва се в UI-а
    // за да покажем "платено от НЗОК" или "платено от пациента"
    private boolean patientInsured;
}
