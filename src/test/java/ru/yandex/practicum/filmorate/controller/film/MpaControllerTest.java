package ru.yandex.practicum.filmorate.controller.film;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.dto.mpa.response.MpaResponseDto;
import ru.yandex.practicum.filmorate.service.mpa.MpaService;

import java.util.Set;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = MpaController.class)
class MpaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MpaService mpaService;

    @Test
    @DisplayName("GET /mpa возвращает HTTP-ответ со статусом 200 и коллекцией c MpaResponseDto")
    void getAllMpa_ReturnsOk() throws Exception {
        MpaResponseDto dto = new MpaResponseDto(1, "R");

        when(mpaService.getAll()).thenReturn(Set.of(dto));

        mockMvc.perform(get("/mpa"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("R"));

        verify(mpaService).getAll();
        verifyNoMoreInteractions(mpaService);
    }

    @Test
    @DisplayName("GET /mpa/{id} возвращает HTTP-ответ со статусом 200 c MpaResponseDto")
    void getMpaById_ReturnsOk() throws Exception {
        MpaResponseDto dto = new MpaResponseDto(1, "R");

        when(mpaService.getById(1)).thenReturn(dto);

        mockMvc.perform(get("/mpa/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("R"));

        verify(mpaService).getById(1);
        verifyNoMoreInteractions(mpaService);
    }


    @Test
    @DisplayName("GET /mpa/{id} возвращает HTTP-ответ со статусом 400 и сообщением 'id не может быть отрицательным'")
    void getMpaById_ReturnsBadRequestWhenIdIsNegative() throws Exception {
        mockMvc.perform(get("/mpa/{id}", -2L))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Ошибка валидации параметров"))
                .andExpect(jsonPath("$.errors.id").value("id mpa не может быть отрицательным"));

        verifyNoInteractions(mpaService);
    }

}