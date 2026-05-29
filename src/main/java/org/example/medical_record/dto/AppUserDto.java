package org.example.medical_record.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppUserDto {

    private Long id;

    @NotBlank(message = "Потребителското име е задължително")
    @Size(min = 3, max = 50)
    private String username;

    // Паролата е задължителна само при създаване
    // При редакция — ако е празна, не се сменя
    @Size(min = 6, message = "Паролата трябва да е поне 6 символа")
    private String password;

    @NotBlank(message = "Изберете роля")
    private String role;  // ROLE_ADMIN, ROLE_DOCTOR, ROLE_PATIENT

    // Опционални връзки
    private Long   doctorId;
    private String doctorName;
    private Long   patientId;
    private String patientName;
}
