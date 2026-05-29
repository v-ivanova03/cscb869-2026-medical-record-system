package org.example.medical_record.service;

import org.example.medical_record.dto.AppUserDto;

import java.util.List;

// Интерфейсът дефинира КАКВО може да прави UserService
// AppUserDetailsService (Spring Security) е ОТДЕЛЕН — той е само за login
// AppUserServiceImpl е за CRUD операциите с потребители
public interface AppUserService {

    List<AppUserDto> getAllUsers();

    AppUserDto getUserById(Long id);

    AppUserDto createUser(AppUserDto dto);

    AppUserDto updateUser(Long id, AppUserDto dto);

    void deleteUser(Long id);
}
