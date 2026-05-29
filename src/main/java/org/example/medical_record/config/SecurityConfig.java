package org.example.medical_record.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
// @EnableMethodSecurity активира @PreAuthorize анотациите в Controller-ите
@EnableMethodSecurity
public class SecurityConfig {

    // PasswordEncoder — хешира паролите (никога не пазим пароли като чист текст!)
    // BCrypt е стандартът — прилага сол и е бавен (нарочно, за сигурност)
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // За проекта ползваме InMemory потребители (достатъчно за курсовата)
    // В реално приложение потребителите са в БД (UserDetailsService + UserRepository)
    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder encoder) {
        return new InMemoryUserDetailsManager(
                User.builder()
                        .username("admin")
                        .password(encoder.encode("admin123"))
                        .roles("ADMIN")       // → ROLE_ADMIN в Spring Security
                        .build(),
                User.builder()
                        .username("doctor1")
                        .password(encoder.encode("doctor123"))
                        .roles("DOCTOR")
                        .build(),
                User.builder()
                        .username("patient1")
                        .password(encoder.encode("patient123"))
                        .roles("PATIENT")
                        .build()
        );
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        // Публично достъпни ресурси (CSS, JS, изображения)
                        .requestMatchers("/css/**", "/static/js/**", "/images/**").permitAll()
                        // Actuator health check — публичен
                        .requestMatchers("/actuator/health").permitAll()
                        // Всичко останало изисква логин
                        .anyRequest().authenticated()
                )
                // Форма за логин (Spring генерира автоматично /login страница)
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/", true)
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutSuccessUrl("/login?logout")
                        .permitAll()
                )
                // CSRF защита — оставяме включена (важно за форми)
                // За чисто REST API обикновено се изключва, но тук имаме и Thymeleaf форми
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/api/**") // API endpoint-ите не ползват форми
                );

        return http.build();
    }
}
