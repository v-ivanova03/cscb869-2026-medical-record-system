package org.example.medical_record.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.medical_record.config.ModelMapperConfig;
import org.example.medical_record.data.entity.Doctor;
import org.example.medical_record.data.repository.DoctorRepository;
import org.example.medical_record.dto.DoctorDto;
import org.example.medical_record.service.DoctorService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

// @Service регистрира класа като Spring bean от тип "service"
// @RequiredArgsConstructor (Lombok) генерира конструктор за final полетата
//   → Spring вижда конструктора и инжектира зависимостите автоматично
@Service
@RequiredArgsConstructor
public class DoctorServiceImpl implements DoctorService {

    private final DoctorRepository doctorRepository;
    private final ModelMapperConfig modelMapperConfig;

    // ------------------------------------------------------------------
    // CRUD операции
    // ------------------------------------------------------------------

    @Override
    public List<DoctorDto> getAllDoctors() {
        List<Doctor> doctors = doctorRepository.findAll();
        // mapList конвертира всеки Doctor → DoctorDto автоматично
        return modelMapperConfig.mapList(doctors, DoctorDto.class);
    }

    @Override
    public DoctorDto getDoctorById(Long id) {
        Doctor doctor = doctorRepository.findById(id)
                // Ако не съществува → хвърляме exception (ще го хванем в Controller-а)
                .orElseThrow(() -> new NoSuchElementException("Лекар с id=" + id + " не съществува"));
        return modelMapperConfig.modelMapper().map(doctor, DoctorDto.class);
    }

    @Override
    public DoctorDto createDoctor(DoctorDto doctorDto) {
        // 1. Конвертираме DTO → Entity
        Doctor doctor = modelMapperConfig.modelMapper().map(doctorDto, Doctor.class);
        // 2. Записваме в БД (save = INSERT ако няма id, UPDATE ако има)
        Doctor saved = doctorRepository.save(doctor);
        // 3. Връщаме назад като DTO (с новото id от БД)
        return modelMapperConfig.modelMapper().map(saved, DoctorDto.class);
    }

    @Override
    public DoctorDto updateDoctor(Long id, DoctorDto doctorDto) {
        Doctor existing = doctorRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Лекар с id=" + id + " не съществува"));

        // Обновяваме само полетата — не сменяме id-то
        existing.setName(doctorDto.getName());
        existing.setSpecialty(doctorDto.getSpecialty());
        existing.setGp(doctorDto.isGp());

        Doctor updated = doctorRepository.save(existing);
        return modelMapperConfig.modelMapper().map(updated, DoctorDto.class);
    }

    @Override
    public void deleteDoctor(Long id) {
        if (!doctorRepository.existsById(id)) {
            throw new NoSuchElementException("Лекар с id=" + id + " не съществува");
        }
        doctorRepository.deleteById(id);
    }

    @Override
    public List<DoctorDto> getGpDoctors() {
        return modelMapperConfig.mapList(
                doctorRepository.findByIsGpTrue(),
                DoctorDto.class
        );
    }

    // ------------------------------------------------------------------
    // Справки
    // ------------------------------------------------------------------

    @Override
    public List<DoctorDto> getDoctorsWithPatientCount() {
        // Заявката връща List<Object[]> → [Doctor, Long]
        return doctorRepository.countPatientsPerGpDoctor().stream()
                .map(row -> {
                    Doctor doctor = (Doctor) row[0];
                    Long count    = (Long)   row[1];
                    DoctorDto dto = modelMapperConfig.modelMapper().map(doctor, DoctorDto.class);
                    dto.setPatientCount(count);
                    return dto;
                })
                .toList();
    }

    @Override
    public List<DoctorDto> getDoctorsWithExaminationCount() {
        return doctorRepository.countExaminationsPerDoctor().stream()
                .map(row -> {
                    Doctor doctor = (Doctor) row[0];
                    Long count    = (Long)   row[1];
                    DoctorDto dto = modelMapperConfig.modelMapper().map(doctor, DoctorDto.class);
                    dto.setExaminationCount(count);
                    return dto;
                })
                .toList();
    }

    @Override
    public List<DoctorDto> getDoctorsWithMostSickLeaves() {
        return modelMapperConfig.mapList(
                doctorRepository.findDoctorsWithMostSickLeaves(),
                DoctorDto.class
        );
    }
}
