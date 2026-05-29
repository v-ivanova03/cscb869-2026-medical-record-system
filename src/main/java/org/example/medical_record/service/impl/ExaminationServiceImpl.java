package org.example.medical_record.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.medical_record.config.ModelMapperConfig;
import org.example.medical_record.data.entity.*;
import org.example.medical_record.data.repository.*;
import org.example.medical_record.dto.ExaminationDto;
import org.example.medical_record.dto.SickLeaveDto;
import org.example.medical_record.service.ExaminationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class ExaminationServiceImpl implements ExaminationService {

    private final ExaminationRepository examinationRepository;
    private final DoctorRepository      doctorRepository;
    private final PatientRepository     patientRepository;
    private final DiagnosisRepository   diagnosisRepository;
    private final ModelMapperConfig     modelMapperConfig;

    @Override
    public List<ExaminationDto> getAllExaminations() {
        return examinationRepository.findAll().stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    public ExaminationDto getExaminationById(Long id) {
        return toDto(examinationRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Преглед с id=" + id + " не съществува")));
    }

    // @Transactional — всички операции в метода са в ЕДНА транзакция:
    // ако нещо гръмне по средата, ВСИЧКО се rollback-ва
    @Override
    @Transactional
    public ExaminationDto createExamination(ExaminationDto dto) {
        Examination examination = buildExaminationFromDto(dto);

        // -------------------------------------------------------
        // БИЗНЕС ЛОГИКА от заданието:
        // Ако пациентът има здравни осигуровки → плаща НЗОК (цена = 0 за пациента)
        // Ако няма → плаща сам (цената остава каквато е зададена от лекаря)
        // -------------------------------------------------------
        if (examination.getPatient().isInsured()) {
            // Цената е реална (колко лекарят взима от НЗОК),
            // но за пациента е 0. Записваме действителната цена за статистика.
            examination.setPrice(dto.getPrice() != null ? dto.getPrice() : BigDecimal.ZERO);
        }

        return toDto(examinationRepository.save(examination));
    }

    @Override
    @Transactional
    public ExaminationDto updateExamination(Long id, ExaminationDto dto) {
        // Само лекарят, направил прегледа, може да го редактира
        // (проверката за права е в SecurityConfig, но може и тук)
        Examination existing = examinationRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Преглед с id=" + id + " не съществува"));

        existing.setDate(dto.getDate());
        existing.setPrice(dto.getPrice());
        existing.setTreatment(dto.getTreatment());

        // Обновяваме диагнозата ако е сменена
        if (dto.getDiagnosisId() != null) {
            Diagnosis diag = diagnosisRepository.findById(dto.getDiagnosisId())
                    .orElseThrow(() -> new NoSuchElementException("Диагноза не съществува"));
            existing.setDiagnosis(diag);
        }

        // Обновяваме болничния ако има такъв в dto
        if (dto.getSickLeave() != null) {
            SickLeave sl = existing.getSickLeave() != null
                    ? existing.getSickLeave()
                    : new SickLeave();
            sl.setStartDate(dto.getSickLeave().getStartDate());
            sl.setTotalDays(dto.getSickLeave().getTotalDays());
            existing.setSickLeave(sl);
        }

        return toDto(examinationRepository.save(existing));
    }

    @Override
    public void deleteExamination(Long id) {
        if (!examinationRepository.existsById(id)) {
            throw new NoSuchElementException("Преглед с id=" + id + " не съществува");
        }
        // CascadeType.ALL на sickLeave → болничният се изтрива автоматично
        examinationRepository.deleteById(id);
    }

    @Override
    public List<ExaminationDto> getPatientHistory(Long patientId) {
        return examinationRepository.findPatientHistory(patientId).stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    public List<ExaminationDto> getByDoctorAndPeriod(Long doctorId, LocalDate from, LocalDate to) {
        return examinationRepository.findByDoctorAndPeriod(doctorId, from, to).stream()
                .map(this::toDto)
                .toList();
    }

    // ------------------------------------------------------------------
    // Помощни методи
    // ------------------------------------------------------------------

    private Examination buildExaminationFromDto(ExaminationDto dto) {
        Doctor doctor = doctorRepository.findById(dto.getDoctorId())
                .orElseThrow(() -> new NoSuchElementException("Лекар не съществува"));
        Patient patient = patientRepository.findById(dto.getPatientId())
                .orElseThrow(() -> new NoSuchElementException("Пациент не съществува"));
        Diagnosis diagnosis = diagnosisRepository.findById(dto.getDiagnosisId())
                .orElseThrow(() -> new NoSuchElementException("Диагноза не съществува"));

        Examination exam = Examination.builder()
                .date(dto.getDate() != null ? dto.getDate() : LocalDate.now())
                .price(dto.getPrice() != null ? dto.getPrice() : BigDecimal.ZERO)
                .treatment(dto.getTreatment())
                .doctor(doctor)
                .patient(patient)
                .diagnosis(diagnosis)
                .build();

        // Болничен лист (незадължителен)
        if (dto.getSickLeave() != null) {
            SickLeave sl = SickLeave.builder()
                    .startDate(dto.getSickLeave().getStartDate())
                    .totalDays(dto.getSickLeave().getTotalDays())
                    .build();
            exam.setSickLeave(sl);
        }

        return exam;
    }

    // Ръчно попълване на display полетата в DTO-то
    private ExaminationDto toDto(Examination e) {
        ExaminationDto dto = modelMapperConfig.modelMapper().map(e, ExaminationDto.class);
        dto.setDoctorId(e.getDoctor().getId());
        dto.setDoctorName(e.getDoctor().getName());
        dto.setPatientId(e.getPatient().getId());
        dto.setPatientName(e.getPatient().getName());
        dto.setPatientInsured(e.getPatient().isInsured());
        dto.setDiagnosisId(e.getDiagnosis().getId());
        dto.setDiagnosisName(e.getDiagnosis().getName());

        if (e.getSickLeave() != null) {
            dto.setSickLeave(modelMapperConfig.modelMapper()
                    .map(e.getSickLeave(), SickLeaveDto.class));
        }
        return dto;
    }
}

