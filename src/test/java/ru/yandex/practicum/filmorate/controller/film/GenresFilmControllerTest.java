package ru.yandex.practicum.filmorate.controller.film;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.dto.genre.response.GenreResponseDto;
import ru.yandex.practicum.filmorate.service.genres.GenresService;
import ru.yandex.practicum.filmorate.web.controller.film.GenresFilmController;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = GenresFilmController.class)
class GenresFilmControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GenresService genresService;

    @Test
    @DisplayName("GET /genres возвращает HTTP-ответ со статусом 200 и коллекцией c GenreResponseDto")
    void getAllGenres_ReturnsOk() throws Exception {
        GenreResponseDto dto = new GenreResponseDto(1, "Комедия");

        when(genresService.getAll()).thenReturn(List.of(dto));

        mockMvc.perform(get("/genres"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Комедия"));

        verify(genresService).getAll();
        verifyNoMoreInteractions(genresService);
    }

    @Test
    @DisplayName("GET /genres/{id} возвращает HTTP-ответ со статусом 200 c GenreResponseDto")
    void getGenreById_ReturnsOk() throws Exception {
        GenreResponseDto dto = new GenreResponseDto(1, "Комедия");

        when(genresService.getById(1)).thenReturn(dto);

        mockMvc.perform(get("/genres/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Комедия"));

        verify(genresService).getById(1);
        verifyNoMoreInteractions(genresService);
    }


    @Test
    @DisplayName("GET /genres/{id} возвращает HTTP-ответ со статусом 400 и сообщением 'id genre не может быть отрицательным'")
    void getGenreById_ReturnsBadRequestWhenIdIsNegative() throws Exception {
        mockMvc.perform(get("/genres/{id}", -2L))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Ошибка валидации параметров"))
                .andExpect(jsonPath("$.errors.id").value("id genre не может быть отрицательным"));

        verifyNoInteractions(genresService);
    }


}