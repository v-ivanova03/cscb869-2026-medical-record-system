package org.example.medical_record.data.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;

@Entity                         // Казва на JPA: "тази Java класа е таблица в БД"
@Table(name = "doctors")        // Името на таблицата в MySQL
@Data                           // Lombok: генерира get/set/equals/hashCode/toString
@NoArgsConstructor              // Lombok: празен конструктор (задължителен за JPA)
@AllArgsConstructor             // Lombok: конструктор с всички полета
@Builder                        // Lombok: Doctor.builder().name("Иван").build()
public class Doctor {

    @Id                                                 // Това поле е primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY) // AUTO_INCREMENT в MySQL
    private Long id;

    @NotBlank(message = "Името е задължително")
    @Size(max = 100, message = "Максимум 100 символа")
    @Column(nullable = false)   // NOT NULL в таблицата
    private String name;

    @NotBlank(message = "Специалността е задължителна")
    @Column(nullable = false)
    private String specialty;

    // Дали лекарят може да бъде личен лекар
    // (само GP лекарите се показват при избор на личен лекар)
    @Column(name = "is_gp", nullable = false)
    private boolean isGp = false;

    // -------------------------------------------------------
    // Връзки (Relations)
    // -------------------------------------------------------

    // Един лекар има МНОГО пациенти (като личен лекар)
    // mappedBy = "gpDoctor" означава: полето gpDoctor в Patient
    //            държи foreign key-а, не Doctor
    // fetch = LAZY означава: пациентите НЕ се зареждат автоматично
    //         (зареждат се само при нужда — по-добра производителност)
    @OneToMany(mappedBy = "gpDoctor", fetch = FetchType.LAZY)
    @ToString.Exclude   // Lombok: изключи от toString() за да няма безкраен цикъл
    private List<Patient> patients;

    // Един лекар е извършил МНОГО прегледи
    @OneToMany(mappedBy = "doctor", fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<Examination> examinations;
}
