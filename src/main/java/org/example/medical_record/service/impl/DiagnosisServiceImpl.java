package org.example.medical_record.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.medical_record.config.ModelMapperConfig;
import org.example.medical_record.data.entity.Diagnosis;
import org.example.medical_record.data.repository.DiagnosisRepository;
import org.example.medical_record.dto.DiagnosisDto;
import org.example.medical_record.service.DiagnosisService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DiagnosisServiceImpl implements DiagnosisService {

    private final DiagnosisRepository diagnosisRepository;
    private final ModelMapperConfig   modelMapperConfig;

    @Override
    public List<DiagnosisDto> getAllDiagnoses() {
        return modelMapperConfig.mapList(diagnosisRepository.findAll(), DiagnosisDto.class);
    }

    @Override
    public DiagnosisDto getDiagnosisById(Long id) {
        Diagnosis d = diagnosisRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Диагноза с id=" + id + " не съществува"));
        return modelMapperConfig.modelMapper().map(d, DiagnosisDto.class);
    }

    @Override
    public DiagnosisDto createDiagnosis(DiagnosisDto dto) {
        Diagnosis diagnosis = modelMapperConfig.modelMapper().map(dto, Diagnosis.class);
        return modelMapperConfig.modelMapper()
                .map(diagnosisRepository.save(diagnosis), DiagnosisDto.class);
    }

    @Override
    public DiagnosisDto updateDiagnosis(Long id, DiagnosisDto dto) {
        Diagnosis existing = diagnosisRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Диагноза с id=" + id + " не съществува"));
        existing.setName(dto.getName());
        existing.setDescription(dto.getDescription());
        return modelMapperConfig.modelMapper()
                .map(diagnosisRepository.save(existing), DiagnosisDto.class);
    }

    @Override
    public void deleteDiagnosis(Long id) {
        if (!diagnosisRepository.existsById(id)) {
            throw new NoSuchElementException("Диагноза с id=" + id + " не съществува");
        }
        diagnosisRepository.deleteById(id);
    }

    // Връща Optional.empty() ако няма прегледи в системата
    // Извикващият код (HomeController, StatsMvcController) решава какво да покаже
    @Override
    public Optional<DiagnosisDto> getMostCommonDiagnosis() {
        return diagnosisRepository.findMostCommonDiagnosis()
                .map(d -> modelMapperConfig.modelMapper().map(d, DiagnosisDto.class));
    }

    @Override
    public List<DiagnosisDto> getAllWithCount() {
        return diagnosisRepository.findAllWithExaminationCount().stream()
                .map(row -> {
                    DiagnosisDto dto = modelMapperConfig.modelMapper()
                            .map((Diagnosis) row[0], DiagnosisDto.class);
                    dto.setExaminationCount((Long) row[1]);
                    return dto;
                })
                .toList();
    }
}
