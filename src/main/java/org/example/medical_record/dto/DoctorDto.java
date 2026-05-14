package org.example.medical_record.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

// DTO (Data Transfer Object) — обектът, който "излиза навън"
// Никога не излагаме директно Entity класовете пред клиента защото:
//   1. Entity-тата имат JPA анотации и може да причинят JSON проблеми
//   2. Може да искаме да покажем само ЧАСТ от полетата
//   3. Разделяме "как съхраняваме" от "как показваме"
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DoctorDto {

    private Long id;

    @NotBlank(message = "Името е задължително")
    @Size(max = 100)
    private String name;

    @NotBlank(message = "Специалността е задължителна")
    private String specialty;

    private boolean isGp;

    // Изчислени полета за справките — не се съхраняват в БД
    private Long patientCount;
    private Long examinationCount;
}
