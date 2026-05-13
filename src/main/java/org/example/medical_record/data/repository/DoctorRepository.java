package org.example.medical_record.data.repository;

import org.example.medical_record.data.entity.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

// JpaRepository<Doctor, Long>:
//   Doctor = entity типа
//   Long   = типа на primary key-а (@Id)
// Spring автоматично генерира: findAll, findById, save, delete и още ~10 метода
@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {

    // -----------------------------------------------------------
    // Методи от ИМЕТО (Spring пише SQL сам)
    // -----------------------------------------------------------

    // SELECT * FROM doctors WHERE is_gp = true
    // Ползва се при "избери личен лекар" в регистрация на пациент
    List<Doctor> findByIsGpTrue();

    // SELECT * FROM doctors WHERE specialty = ?
    List<Doctor> findBySpecialty(String specialty);

    // -----------------------------------------------------------
    // Custom JPQL заявки (@Query)
    // JPQL е като SQL, но вместо таблици ползва Java класове
    // -----------------------------------------------------------

    // Справка: брой пациенти при всеки личен лекар
    // GROUP BY d → за всеки лекар, COUNT колко пациенти имат gpDoctor = него
    @Query("SELECT d, COUNT(p) FROM Doctor d LEFT JOIN d.patients p GROUP BY d")
    List<Object[]> countPatientsPerGpDoctor();

    // Справка: брой посещения при всеки лекар
    // (колко прегледа е направил всеки лекар)
    @Query("SELECT d, COUNT(e) FROM Doctor d LEFT JOIN d.examinations e GROUP BY d")
    List<Object[]> countExaminationsPerDoctor();

    // Справка: лекар(и) с най-много издадени болнични
    // Подзаявката намира максималния брой, външната взима лекарите с него
    @Query("""
            SELECT d FROM Doctor d
            JOIN d.examinations e
            WHERE e.sickLeave IS NOT NULL
            GROUP BY d
            HAVING COUNT(e.sickLeave) = (
                SELECT MAX(cnt) FROM (
                    SELECT COUNT(e2.sickLeave) AS cnt
                    FROM Doctor d2
                    JOIN d2.examinations e2
                    WHERE e2.sickLeave IS NOT NULL
                    GROUP BY d2
                )
            )
            """)
    List<Doctor> findDoctorsWithMostSickLeaves();
}
