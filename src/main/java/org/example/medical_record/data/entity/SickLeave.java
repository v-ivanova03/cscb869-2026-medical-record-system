package org.example.medical_record.data.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "sick_leaves")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SickLeave {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Началната дата е задължителна")
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;   // LocalDate → само дата без час

    @Min(value = 1, message = "Минимум 1 ден")
    @Column(name = "total_days", nullable = false)
    private int totalDays;

    // Болничният е свързан с точно един преглед (OneToOne)
    // mappedBy = "sickLeave" означава: в Examination е FK-а
    @OneToOne(mappedBy = "sickLeave", fetch = FetchType.LAZY)
    @ToString.Exclude
    private Examination examination;
}
