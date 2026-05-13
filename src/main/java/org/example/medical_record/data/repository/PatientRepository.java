package org.example.medical_record.data.repository;

import org.example.medical_record.data.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {

    // -----------------------------------------------------------
    // Методи от ИМЕТО
    // -----------------------------------------------------------

    // SELECT * FROM patients WHERE egn = ?
    // Optional защото може да няма пациент с такова ЕГН
    Optional<Patient> findByEgn(String egn);

    // SELECT * FROM patients WHERE gp_doctor_id = ?
    // Справка: пациенти към даден личен лекар
    List<Patient> findByGpDoctorId(Long gpDoctorId);

    // -----------------------------------------------------------
    // Custom JPQL заявки
    // -----------------------------------------------------------

    // Справка: списък на пациенти с дадена диагноза
    // Вървим по: Patient → examinations → diagnosis → name
    // DISTINCT защото един пациент може да има диагнозата многократно
    @Query("""
            SELECT DISTINCT p FROM Patient p
            JOIN p.examinations e
            JOIN e.diagnosis d
            WHERE d.name = :diagnosisName
            """)
    List<Patient> findPatientsByDiagnosisName(@Param("diagnosisName") String diagnosisName);

    // Справка: обща стойност на прегледите платени ОТ ПАЦИЕНТИТЕ
    // (само тези, които не са осигурени → insured = false)
    @Query("""
            SELECT SUM(e.price) FROM Examination e
            JOIN e.patient p
            WHERE p.insured = false
            """)
    Double getTotalPriceForUninsuredPatients();

    // Справка: стойност платена от пациентите СПОРЕД ЛЕКАРЯ
    // Връща [лекар, сума] за всеки лекар
    @Query("""
            SELECT e.doctor, SUM(e.price) FROM Examination e
            JOIN e.patient p
            WHERE p.insured = false
            GROUP BY e.doctor
            """)
    List<Object[]> getTotalPricePerDoctorForUninsuredPatients();
}
