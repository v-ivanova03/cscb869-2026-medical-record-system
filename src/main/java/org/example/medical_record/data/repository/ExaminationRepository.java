package org.example.medical_record.data.repository;

import org.example.medical_record.data.entity.Examination;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ExaminationRepository extends JpaRepository<Examination, Long> {

    // -----------------------------------------------------------
    // Методи от ИМЕТО
    // -----------------------------------------------------------

    // История на посещенията на пациент (по id)
    List<Examination> findByPatientId(Long patientId);

    // Прегледи по лекар
    List<Examination> findByDoctorId(Long doctorId);

    // Прегледи по лекар И период (от дата до дата)
    // Spring генерира: WHERE doctor_id = ? AND date BETWEEN ? AND ?
    List<Examination> findByDoctorIdAndDateBetween(Long doctorId, LocalDate from, LocalDate to);

    // Прегледи само по период
    List<Examination> findByDateBetween(LocalDate from, LocalDate to);

    // -----------------------------------------------------------
    // Custom JPQL заявки
    // -----------------------------------------------------------

    // Справка: история на посещенията — зарежда и лекар, и диагноза
    // наведнъж (JOIN FETCH = eager зареждане само за тази заявка,
    // избягва N+1 проблема)
    @Query("""
            SELECT e FROM Examination e
            JOIN FETCH e.doctor
            JOIN FETCH e.diagnosis
            WHERE e.patient.id = :patientId
            ORDER BY e.date DESC
            """)
    List<Examination> findPatientHistory(@Param("patientId") Long patientId);

    // Прегледи по лекар и/или период (и двата параметъра са незадължителни)
    // :#{#doctorId == null} позволява nullable параметри
    @Query("""
            SELECT e FROM Examination e
            WHERE (:doctorId IS NULL OR e.doctor.id = :doctorId)
              AND (:from IS NULL OR e.date >= :from)
              AND (:to IS NULL OR e.date <= :to)
            ORDER BY e.date DESC
            """)
    List<Examination> findByDoctorAndPeriod(
            @Param("doctorId") Long doctorId,
            @Param("from") LocalDate from,
            @Param("to") LocalDate to
    );
}