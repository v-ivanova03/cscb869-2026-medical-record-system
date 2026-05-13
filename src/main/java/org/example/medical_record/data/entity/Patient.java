package org.example.medical_record.data.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "patients")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Името е задължително")
    @Size(max = 100)
    @Column(nullable = false)
    private String name;

    // ЕГН — уникално, точно 10 цифри
    @NotBlank(message = "ЕГН е задължително")
    @Pattern(regexp = "\\d{10}", message = "ЕГН трябва да е точно 10 цифри")
    @Column(unique = true, nullable = false, length = 10)
    private String egn;

    // Здравноосигурителен статус за последните 6 месеца
    // true  = пациентът има осигуровки → плаща НЗОК
    // false = пациентът НЯМА осигуровки → плаща сам
    @Column(nullable = false)
    private boolean insured = false;

    // -------------------------------------------------------
    // Връзки
    // -------------------------------------------------------

    // Много пациенти → един личен лекар  (ManyToOne)
    // @JoinColumn казва: в таблица "patients" има колона "gp_doctor_id"
    //             която е foreign key към таблица "doctors"
    // nullable = true защото пациент може временно да няма личен лекар
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gp_doctor_id", nullable = true)
    @ToString.Exclude
    private Doctor gpDoctor;

    // Един пациент има МНОГО прегледи
    @OneToMany(mappedBy = "patient", fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<Examination> examinations;
}
