package org.example.medical_record.service;

import org.example.medical_record.dto.SickLeaveDto;

import java.util.List;
import java.util.Map;

public interface SickLeaveService {

    List<SickLeaveDto> getAllSickLeaves();
    SickLeaveDto getSickLeaveById(Long id);
    void deleteSickLeave(Long id);

    // Справка: месец с най-много болнични
    // Връща Map с ключове "year", "month", "count"
    Map<String, Object> getMonthWithMostSickLeaves();
}
