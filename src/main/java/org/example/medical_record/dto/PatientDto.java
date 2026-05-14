package org.example.medical_record.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PatientDto {

    private Long id;

    @NotBlank(message = "Името е задължително")
    private String name;

    @NotBlank(message = "ЕГН е задължително")
    @Pattern(regexp = "\\d{10}", message = "ЕГН трябва да е точно 10 цифри")
    private String egn;

    private boolean insured;

    // Показваме само id и name на личния лекар — не целия DoctorDto
    // (избягваме безкрайно вгнездване)
    private Long gpDoctorId;
    private String gpDoctorName;
}
