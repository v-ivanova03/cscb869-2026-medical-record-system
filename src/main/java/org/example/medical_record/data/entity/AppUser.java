package org.example.medical_record.data.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

// Потребителският акаунт в системата
// Отделен от Patient/Doctor — един лекар може да има акаунт,
// друг може да няма; пациентите може да нямат достъп изобщо
@Entity
@Table(name = "app_users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(unique = true, nullable = false)
    private String username;

    @NotBlank
    @Column(nullable = false)
    private String password;   // BCrypt хеш — никога plain text

    // Ролята: ROLE_ADMIN, ROLE_DOCTOR, ROLE_PATIENT
    @Column(nullable = false)
    private String role;

    // Връзка към лекар (ако потребителят е лекар) — незадължително
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id")
    @ToString.Exclude
    private Doctor doctor;

    // Връзка към пациент (ако потребителят е пациент) — незадължително
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id")
    @ToString.Exclude
    private Patient patient;

    public boolean isAdmin()   { return "ROLE_ADMIN".equals(role); }
    public boolean isDoctor()  { return "ROLE_DOCTOR".equals(role); }
    public boolean isPatient() { return "ROLE_PATIENT".equals(role); }
}