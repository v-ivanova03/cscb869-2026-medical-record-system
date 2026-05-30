package org.example.medical_record.service.impl;

import org.example.medical_record.data.entity.AppUser;
import org.example.medical_record.data.repository.AppUserRepository;
import org.example.medical_record.data.repository.DoctorRepository;
import org.example.medical_record.data.repository.PatientRepository;
import org.example.medical_record.dto.AppUserDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(SpringExtension.class)
class AppUserServiceImplTest {

    @Mock AppUserRepository userRepository;
    @Mock DoctorRepository  doctorRepository;
    @Mock PatientRepository patientRepository;
    @Mock PasswordEncoder   passwordEncoder;

    @InjectMocks AppUserServiceImpl userService;

    @Test
    void createUser_throwsWhenUsernameExists() {
        Mockito.when(userRepository.findByUsername("admin"))
                .thenReturn(Optional.of(new AppUser()));

        AppUserDto dto = AppUserDto.builder()
                .username("admin").password("pass").role("ROLE_ADMIN").build();

        assertThrows(IllegalArgumentException.class, () -> userService.createUser(dto));
    }

    @Test
    void createUser_savesWithEncodedPassword() {
        Mockito.when(userRepository.findByUsername("newuser")).thenReturn(Optional.empty());
        Mockito.when(passwordEncoder.encode("pass123")).thenReturn("$2a$hashed");
        Mockito.when(userRepository.save(any())).thenAnswer(inv -> {
            AppUser u = inv.getArgument(0);
            u.setId(1L);
            return u;
        });

        AppUserDto dto = AppUserDto.builder()
                .username("newuser").password("pass123").role("ROLE_DOCTOR").build();

        AppUserDto result = userService.createUser(dto);

        assertEquals("newuser", result.getUsername());
        assertNull(result.getPassword()); // паролата не се връща
        Mockito.verify(passwordEncoder).encode("pass123");
    }

    @Test
    void updateUser_throwsWhenNotFound() {
        Mockito.when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class,
                () -> userService.updateUser(99L, new AppUserDto()));
    }

    @Test
    void updateUser_doesNotChangePasswordWhenBlank() {
        AppUser existing = AppUser.builder()
                .id(1L).username("doc1").password("$old_hash").role("ROLE_DOCTOR").build();

        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(existing));
        Mockito.when(userRepository.save(any())).thenReturn(existing);

        AppUserDto dto = AppUserDto.builder()
                .role("ROLE_DOCTOR").password("").build(); // празна парола

        userService.updateUser(1L, dto);

        // passwordEncoder.encode НЕ трябва да се е извикал
        Mockito.verify(passwordEncoder, Mockito.never()).encode(any());
        assertEquals("$old_hash", existing.getPassword());
    }

    @Test
    void deleteUser_throwsWhenNotFound() {
        Mockito.when(userRepository.existsById(99L)).thenReturn(false);

        assertThrows(NoSuchElementException.class, () -> userService.deleteUser(99L));
    }
}