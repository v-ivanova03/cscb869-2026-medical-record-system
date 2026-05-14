package org.example.medical_record.data.repository;

import org.example.medical_record.data.entity.SickLeave;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SickLeaveRepository extends JpaRepository<SickLeave, Long> {

    // -----------------------------------------------------------
    // Справка: месец с най-много издадени болнични
    //
    // FUNCTION('MONTH', s.startDate) → извлича месеца от датата
    // FUNCTION('YEAR',  s.startDate) → извлича годината
    // Групираме по година+месец, сортираме по брой DESC, взимаме 1
    // -----------------------------------------------------------
    @Query("""
            SELECT FUNCTION('YEAR', s.startDate),
                   FUNCTION('MONTH', s.startDate),
                   COUNT(s)
            FROM SickLeave s
            GROUP BY FUNCTION('YEAR', s.startDate), FUNCTION('MONTH', s.startDate)
            ORDER BY COUNT(s) DESC
            LIMIT 1
            """)
    List<Object[]> findMonthWithMostSickLeaves();

    // Всички болнични за даден преглед (по examination id)
    // (ползва се при изтриване на преглед)
    SickLeave findByExaminationId(Long examinationId);
}