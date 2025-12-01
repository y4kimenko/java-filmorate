package ru.yandex.practicum.filmorate.controller.film;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = FilmController.class)
class FilmControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private FilmService filmService;


    @Test
    @DisplayName("GET /films/popular возвращает HTTP-ответ со статусом 200 OK и списком film")
    void getPopularFilms_ReturnsOkWithListFromService() throws Exception {
        Film film = new Film();
        film.setId(42L);
        film.setName("Test Film");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);

        when(filmService.getPopularFilms(5)).thenReturn(List.of(film));

        mockMvc.perform(get("/films/popular").param("count", "5"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(42))
                .andExpect(jsonPath("$[0].name").value("Test Film"))
                .andExpect(jsonPath("$[0].description").value("Description"))
                .andExpect(jsonPath("$[0].duration").value(120))
                .andExpect(jsonPath("$[0].releaseDate").value("2000-01-01"));
    }

    @Test
    @DisplayName("GET /films/popular возвращает HTTP-ответ со статусом 400 и описанием ошибки 'count  не может быть отрицательным'")
    void getPopularFilms_ReturnsBadRequestWhenCountNegative() throws Exception {
        mockMvc.perform(get("/films/popular").param("count", "-1"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Ошибка валидации параметров"))
                .andExpect(jsonPath("$.errors.count").value("count  не может быть отрицательным"));

        verifyNoInteractions(filmService);
    }


    @Test
    @DisplayName("POST /films возвращает HTTP-ответ со статусом 200 и созданный film обратно")
    void createFilm_ReturnsCreated() throws Exception {
        Film film = new Film();
        film.setId(42L);
        film.setName("Test Film");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);

        when(filmService.createFilm(any(Film.class))).thenReturn(film);

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(film)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(42))
                .andExpect(jsonPath("$.name").value("Test Film"))
                .andExpect(jsonPath("$.description").value("Description"))
                .andExpect(jsonPath("$.releaseDate").value("2000-01-01"));
    }


    @Test
    @DisplayName("POST /films возвращает HTTP-ответ со статусом 200 и сообщением 'releaseDate не может быть раньше чем 28.12.1895'")
    void createFilm_ReturnsBadRequestWhenReleaseDateTooEarly() throws Exception {
        Film request = new Film();
        request.setName("Old Film");
        request.setDescription("Description");
        request.setReleaseDate(LocalDate.of(1800, 1, 1));
        request.setDuration(90);

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Ошибка валидации входных данных"))
                .andExpect(jsonPath("$.errors.releaseDateValid").value("releaseDate не может быть раньше чем 28.12.1895"));

        verifyNoInteractions(filmService);
    }


    @Test
    @DisplayName("POST /films возвращает HTTP-ответ со статусом 200 и сообщением 'у description максимальная длина 200 символов'")
    void createFilm_ReturnsBadRequestWhenDescriptionTooLong() throws Exception {
        Film request = new Film();
        request.setName("Old Film");
        request.setDescription("a".repeat(201));
        request.setReleaseDate(LocalDate.of(1990, 1, 1));
        request.setDuration(90);

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Ошибка валидации входных данных"))
                .andExpect(jsonPath("$.errors.description").value("у description максимальная длина 200 символов"));

        verifyNoInteractions(filmService);
    }


    @Test
    @DisplayName("POST /films возвращает HTTP-ответ со статусом 200 и сообщением 'name не должно состоять из пробелов'")
    void createFilm_ReturnsBadRequestWhenNameIsBlank() throws Exception {
        Film request = new Film();
        request.setName("");
        request.setDescription("Description");
        request.setReleaseDate(LocalDate.of(1990, 1, 1));
        request.setDuration(90);

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Ошибка валидации входных данных"))
                .andExpect(jsonPath("$.errors.name").value("name не должно состоять из пробелов"));

        verifyNoInteractions(filmService);
    }


    @Test
    @DisplayName("POST /films возвращает HTTP-ответ со статусом 200 и сообщением 'duration не может быть пустым'")
    void createFilm_ReturnsBadRequestWhenDurationIsNull() throws Exception {
        Film request = new Film();
        request.setName("Old Film");
        request.setDescription("Description");
        request.setReleaseDate(LocalDate.of(1990, 1, 1));
        request.setDuration(null);

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Ошибка валидации входных данных"))
                .andExpect(jsonPath("$.errors.duration").value("duration не может быть пустым"));

        verifyNoInteractions(filmService);
    }


    @Test
    @DisplayName("POST /films возвращает HTTP-ответ со статусом 200 и сообщением 'duration должна составлять не меньше 1 минуты'")
    void createFilm_ReturnsBadRequestWhenDurationIsNegative() throws Exception {
        Film request = new Film();
        request.setName("Old Film");
        request.setDescription("Description");
        request.setReleaseDate(LocalDate.of(1990, 1, 1));
        request.setDuration(-1);

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Ошибка валидации входных данных"))
                .andExpect(jsonPath("$.errors.duration").value("duration должна составлять не меньше 1 минуты"));

        verifyNoInteractions(filmService);
    }


    @Test
    @DisplayName("PUT /film возвращает HTTP-ответ со статусом 200 и обновлённым film")
    void updateFilm_ReturnsOk() throws Exception {
        Film request = new Film();
        request.setId(42L);
        request.setName("Test Film");
        request.setDescription("Description");
        request.setReleaseDate(LocalDate.of(2000, 1, 1));
        request.setDuration(120);

        when(filmService.updateFilm(any(Film.class))).thenReturn(request);

        mockMvc.perform(put("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(42))
                .andExpect(jsonPath("$.name").value("Test Film"))
                .andExpect(jsonPath("$.description").value("Description"))
                .andExpect(jsonPath("$.duration").value(120))
                .andExpect(jsonPath("$.releaseDate").value("2000-01-01"));

        verify(filmService).updateFilm(any(Film.class));
        verifyNoMoreInteractions(filmService);
    }

    @Test
    @DisplayName("GET /films возвращает HTTP-ответ со статусом 200 и коллекцией c film")
    void getAllFilms_ReturnsOk() throws Exception {
        Film film = new Film();
        film.setId(42L);
        film.setName("Test Film");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);

        when(filmService.getAllFilms()).thenReturn(List.of(film));

        mockMvc.perform(get("/films"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(42))
                .andExpect(jsonPath("$[0].name").value("Test Film"))
                .andExpect(jsonPath("$[0].description").value("Description"))
                .andExpect(jsonPath("$[0].duration").value(120))
                .andExpect(jsonPath("$[0].releaseDate").value("2000-01-01"));

        verify(filmService).getAllFilms();
        verifyNoMoreInteractions(filmService);
    }

    @Test
    @DisplayName("PUT /films/{id}/like/{userId} возвращает HTTP-ответ со статусом 200")
    void addUserLike_ReturnsOkAndDelegatesToService() throws Exception {
        mockMvc.perform(put("/films/{id}/like/{userId}", 1L, 2L))
                .andExpect(status().isOk());

        verify(filmService).addUserLike(1L, 2L);
    }

    @Test
    @DisplayName("PUT /films/{id}/like/{userId} возвращает HTTP-ответ со статусом 400 и сообщением 'id пользователя не может быть отрицательным' и 'id фильма не может быть отрицательным'")
    void addUserLike_ReturnsBadRequestWhenPathVariablesNegative() throws Exception {
        mockMvc.perform(put("/films/{id}/like/{userId}", -1L, -5L))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Ошибка валидации параметров"))
                .andExpect(jsonPath("$.errors.id").value("id фильма не может быть отрицательным"))
                .andExpect(jsonPath("$.errors.userId").value("id пользователя не может быть отрицательным"));

        verifyNoInteractions(filmService);
    }


    @Test
    @DisplayName("DELETE /films/{id}/like/{userId} возвращает HTTP-ответ со статусом 200")
    void removeUserLike_ReturnsOkAndDelegatesToService() throws Exception {
        mockMvc.perform(delete("/films/{id}/like/{userId}", 3L, 4L))
                .andExpect(status().isOk());

        verify(filmService).removeUserLike(3L, 4L);
    }

    @Test
    @DisplayName("DELETE /films/{id}/like/{userId} возвращает HTTP-ответ со статусом 400 сообщением 'id пользователя не может быть отрицательным'")
    void removeUserLikeReturnsBadRequestWhenUserIdNegative() throws Exception {
        mockMvc.perform(delete("/films/{id}/like/{userId}", 10L, -2L))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Ошибка валидации параметров"))
                .andExpect(jsonPath("$.errors.userId").value("id пользователя не может быть отрицательным"));

        verifyNoInteractions(filmService);
    }

    @Test
    @DisplayName("DELETE /films/{id}/like/{userId} возвращает HTTP-ответ со статусом 400 сообщением 'id пользователя не может быть отрицательным'")
    void removeUserLikeReturnsBadRequestWhenFilmIdNegative() throws Exception {
        mockMvc.perform(delete("/films/{id}/like/{userId}", 10L, -2L))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Ошибка валидации параметров"))
                .andExpect(jsonPath("$.errors.userId").value("id пользователя не может быть отрицательным"));

        verifyNoInteractions(filmService);
    }

}