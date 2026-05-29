package org.example.medical_record.data.repository;

import org.example.medical_record.data.entity.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AppUserRepository extends JpaRepository<AppUser, Long> {

    // Spring Security извиква loadUserByUsername(username)
    // затова трябва точно този метод
    Optional<AppUser> findByUsername(String username);
}
