package org.example.medical_record.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.stream.Collectors;

@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    // Помощен метод: конвертира List<Entity> → List<DTO>
    // Пример: mapList(doctors, DoctorDto.class)
    public <S, T> List<T> mapList(List<S> source, Class<T> targetClass) {
        ModelMapper mapper = modelMapper();
        return source.stream()
                .map(element -> mapper.map(element, targetClass))
                .collect(Collectors.toList());
    }
}
