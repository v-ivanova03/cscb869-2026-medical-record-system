package org.example.medical_record.controller.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.medical_record.dto.DoctorDto;
import org.example.medical_record.service.impl.DoctorServiceImpl;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// @WebMvcTest зарежда САМО Web слоя (Controller + Security)
// Service-ът е mock-нат → не вика реална бизнес логика
@WebMvcTest(DoctorApiController.class)
class DoctorApiControllerTest {

    @MockitoBean
    private DoctorServiceImpl doctorService;

    @Autowired
    private MockMvc mockMvc;         // Симулира HTTP заявки

    @Autowired
    private ObjectMapper objectMapper; // JSON сериализация

    @Test
    @WithMockUser(roles = {"ADMIN"}) // Симулира логнат ADMIN потребител
    void getAllDoctors_returnsOkAndList() throws Exception {
        DoctorDto d1 = DoctorDto.builder().id(1L).name("Д-р Иванов").specialty("Хирургия").build();
        DoctorDto d2 = DoctorDto.builder().id(2L).name("Д-р Петрова").specialty("Кардиология").build();

        Mockito.when(doctorService.getAllDoctors()).thenReturn(List.of(d1, d2));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/doctors"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name", is("Д-р Иванов")))
                .andExpect(jsonPath("$[1].specialty", is("Кардиология")));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void createDoctor_returnsCreatedDoctor() throws Exception {
        DoctorDto input  = DoctorDto.builder().name("Д-р Нов").specialty("Неврология").build();
        DoctorDto output = DoctorDto.builder().id(3L).name("Д-р Нов").specialty("Неврология").build();

        Mockito.when(doctorService.createDoctor(any())).thenReturn(output);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/doctors")
                        .with(csrf())                        // CSRF token за POST
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(3)))
                .andExpect(jsonPath("$.name", is("Д-р Нов")));
    }

    @Test
    @WithMockUser(roles = {"PATIENT"}) // PATIENT не може да създава лекари
    void createDoctor_forbiddenForPatient() throws Exception {
        DoctorDto input = DoctorDto.builder().name("Д-р Нов").specialty("Неврология").build();

        mockMvc.perform(MockMvcRequestBuilders.post("/api/doctors")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isForbidden()); // 403 Forbidden
    }
}