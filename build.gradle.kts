plugins {
    java
    id("org.springframework.boot") version "3.5.11"
    id("io.spring.dependency-management") version "1.1.7"
}

group = "org.informatics"
version = "0.0.1-SNAPSHOT"
description = "Medical Record System — CSCB869"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

dependencies {
    // Web + REST
    implementation("org.springframework.boot:spring-boot-starter-web")

    // База данни
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    runtimeOnly("com.mysql:mysql-connector-j")

    // Thymeleaf (HTML изгледи)
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")

    // Валидация (@NotNull, @Size и т.н.)
    implementation("org.springframework.boot:spring-boot-starter-validation")

    // Spring Security (роли: ADMIN, DOCTOR, PATIENT)
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.thymeleaf.extras:thymeleaf-extras-springsecurity6")

    // Actuator (здравен статус на приложението)
    implementation("org.springframework.boot:spring-boot-starter-actuator")

    // ModelMapper (Entity <-> DTO)
    implementation("org.modelmapper:modelmapper:3.2.1")

    // Lombok (@Data, @Builder, @NoArgsConstructor и т.н.)
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")

    // Тестове
    testImplementation("com.h2database:h2")                                       // in-memory БД за тестове
    testImplementation("org.springframework.boot:spring-boot-starter-test")       // JUnit 5 + Mockito
    testImplementation("org.springframework.security:spring-security-test")       // @WithMockUser
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
