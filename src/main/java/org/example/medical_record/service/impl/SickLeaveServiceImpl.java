package org.example.medical_record.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.medical_record.config.ModelMapperConfig;
import org.example.medical_record.data.entity.SickLeave;
import org.example.medical_record.data.repository.SickLeaveRepository;
import org.example.medical_record.dto.SickLeaveDto;
import org.example.medical_record.service.SickLeaveService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class SickLeaveServiceImpl implements SickLeaveService {

    private final SickLeaveRepository sickLeaveRepository;
    private final ModelMapperConfig   modelMapperConfig;

    @Override
    public List<SickLeaveDto> getAllSickLeaves() {
        return modelMapperConfig.mapList(sickLeaveRepository.findAll(), SickLeaveDto.class);
    }

    @Override
    public SickLeaveDto getSickLeaveById(Long id) {
        SickLeave sl = sickLeaveRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Болничен с id=" + id + " не съществува"));
        return modelMapperConfig.modelMapper().map(sl, SickLeaveDto.class);
    }

    @Override
    public void deleteSickLeave(Long id) {
        if (!sickLeaveRepository.existsById(id)) {
            throw new NoSuchElementException("Болничен с id=" + id + " не съществува");
        }
        sickLeaveRepository.deleteById(id);
    }

    @Override
    public Map<String, Object> getMonthWithMostSickLeaves() {
        List<Object[]> results = sickLeaveRepository.findMonthWithMostSickLeaves();
        if (results.isEmpty()) {
            return Map.of("year", 0, "month", 0, "count", 0);
        }
        Object[] row = results.get(0);
        return Map.of(
                "year",  row[0],
                "month", row[1],
                "count", row[2]
        );
    }
}
