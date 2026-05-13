package org.example.medical_record.data.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "diagnoses")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Diagnosis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Кратко наименование, напр. "Грип", "Хипертония"
    @NotBlank(message = "Наименованието е задължително")
    @Column(nullable = false, unique = true)
    private String name;

    // По-подробно описание (незадължително)
    @Column(columnDefinition = "TEXT")
    private String description;

    // Една диагноза може да е поставена на МНОГО прегледи
    @OneToMany(mappedBy = "diagnosis", fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<Examination> examinations;
}
