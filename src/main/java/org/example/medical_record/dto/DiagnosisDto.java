package org.example.medical_record.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DiagnosisDto {

    private Long id;

    @NotBlank(message = "Наименованието е задължително")
    private String name;

    private String description;

    // За справката "най-честа диагноза"
    private Long examinationCount;
}
