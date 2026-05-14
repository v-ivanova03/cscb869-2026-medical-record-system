package org.example.medical_record.data.repository;

import org.example.medical_record.data.entity.Diagnosis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DiagnosisRepository extends JpaRepository<Diagnosis, Long> {

    // SELECT * FROM diagnoses WHERE name = ?
    Optional<Diagnosis> findByName(String name);

    // -----------------------------------------------------------
    // Справка: най-често срещана диагноза
    // Броим колко пъти всяка диагноза се появява в прегледи,
    // сортираме DESC и взимаме само първия резултат — LIMIT 1
    // -----------------------------------------------------------
    @Query("""
            SELECT d FROM Diagnosis d
            JOIN d.examinations e
            GROUP BY d
            ORDER BY COUNT(e) DESC
            LIMIT 1
            """)
    Optional<Diagnosis> findMostCommonDiagnosis();

    // Всички диагнози с брой употреби (за статистика)
    @Query("SELECT d, COUNT(e) FROM Diagnosis d LEFT JOIN d.examinations e GROUP BY d ORDER BY COUNT(e) DESC")
    List<Object[]> findAllWithExaminationCount();
}
