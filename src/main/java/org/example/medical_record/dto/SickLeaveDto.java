package org.example.medical_record.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SickLeaveDto {

    private Long id;

    @NotNull(message = "Началната дата е задължителна")
    private LocalDate startDate;

    @Min(value = 1, message = "Минимум 1 ден")
    private int totalDays;
}
