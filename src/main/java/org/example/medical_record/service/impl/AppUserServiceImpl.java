package org.example.medical_record.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.medical_record.data.entity.AppUser;
import org.example.medical_record.data.entity.Doctor;
import org.example.medical_record.data.entity.Patient;
import org.example.medical_record.data.repository.AppUserRepository;
import org.example.medical_record.data.repository.DoctorRepository;
import org.example.medical_record.data.repository.PatientRepository;
import org.example.medical_record.dto.AppUserDto;
import org.example.medical_record.service.AppUserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class AppUserServiceImpl implements AppUserService {

    private final AppUserRepository userRepository;
    private final DoctorRepository  doctorRepository;
    private final PatientRepository patientRepository;
    private final PasswordEncoder   passwordEncoder;

    @Override
    public List<AppUserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    public AppUserDto getUserById(Long id) {
        return toDto(userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException(
                        "Потребител с id=" + id + " не съществува")));
    }

    @Override
    @Transactional
    public AppUserDto createUser(AppUserDto dto) {
        // Проверяваме дали username вече съществува
        if (userRepository.findByUsername(dto.getUsername()).isPresent()) {
            throw new IllegalArgumentException(
                    "Потребител с username '" + dto.getUsername() + "' вече съществува");
        }

        AppUser user = AppUser.builder()
                .username(dto.getUsername())
                // Хешираме паролата — никога plain text в БД
                .password(passwordEncoder.encode(dto.getPassword()))
                .role(dto.getRole())
                .build();

        linkDoctorAndPatient(user, dto);
        return toDto(userRepository.save(user));
    }

    @Override
    @Transactional
    public AppUserDto updateUser(Long id, AppUserDto dto) {
        AppUser existing = userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException(
                        "Потребител с id=" + id + " не съществува"));

        existing.setRole(dto.getRole());

        // Паролата се сменя само ако е подадена нова
        // Ако полето е празно — запазваме старата
        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            existing.setPassword(passwordEncoder.encode(dto.getPassword()));
        }

        linkDoctorAndPatient(existing, dto);
        return toDto(userRepository.save(existing));
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new NoSuchElementException("Потребител с id=" + id + " не съществува");
        }
        userRepository.deleteById(id);
    }

    // ------------------------------------------------------------------
    // Помощни методи
    // ------------------------------------------------------------------

    // Свързва потребителя с лекар и/или пациент
    private void linkDoctorAndPatient(AppUser user, AppUserDto dto) {
        if (dto.getDoctorId() != null) {
            Doctor doc = doctorRepository.findById(dto.getDoctorId())
                    .orElseThrow(() -> new NoSuchElementException("Лекар не съществува"));
            user.setDoctor(doc);
        } else {
            user.setDoctor(null);
        }

        if (dto.getPatientId() != null) {
            Patient pat = patientRepository.findById(dto.getPatientId())
                    .orElseThrow(() -> new NoSuchElementException("Пациент не съществува"));
            user.setPatient(pat);
        } else {
            user.setPatient(null);
        }
    }

    // Конвертира AppUser entity → AppUserDto
    private AppUserDto toDto(AppUser user) {
        AppUserDto dto = AppUserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .role(user.getRole())
                .build();

        // Паролата НИКОГА не се връща към клиента
        // dto.setPassword(null) — оставяме го null

        if (user.getDoctor() != null) {
            dto.setDoctorId(user.getDoctor().getId());
            dto.setDoctorName(user.getDoctor().getName());
        }
        if (user.getPatient() != null) {
            dto.setPatientId(user.getPatient().getId());
            dto.setPatientName(user.getPatient().getName());
        }
        return dto;
    }
}