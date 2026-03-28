package ru.yandex.practicum.filmorate.controller.film;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.dto.director.request.DirectorRequestDto;
import ru.yandex.practicum.filmorate.dto.director.response.DirectorResponseDto;
import ru.yandex.practicum.filmorate.dto.film.request.FilmRequestCreateDto;
import ru.yandex.practicum.filmorate.dto.film.request.FilmRequestUpdateDto;
import ru.yandex.practicum.filmorate.dto.film.response.FilmResponseDto;
import ru.yandex.practicum.filmorate.dto.genre.request.GenreRequestDto;
import ru.yandex.practicum.filmorate.dto.genre.response.GenreResponseDto;
import ru.yandex.practicum.filmorate.dto.mpa.request.MpaRequestDto;
import ru.yandex.practicum.filmorate.dto.mpa.response.MpaResponseDto;
import ru.yandex.practicum.filmorate.service.film.FilmService;
import ru.yandex.practicum.filmorate.web.controller.film.FilmController;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = FilmController.class)
class FilmControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private FilmService filmService;


    @Test
    @DisplayName("GET /films/popular возвращает HTTP-ответ со статусом 200 OK и списком FilmResponseDto")
    void getPopularFilms_ReturnsOkWithListFromService() throws Exception {
        FilmResponseDto film = new FilmResponseDto(
                42L,
                "Test Film",
                List.of(new GenreResponseDto(1, "Комедия")),
                new MpaResponseDto(1, "R"),
                List.of(new DirectorResponseDto(1, "Danila Petrovich")),
                "Description",
                LocalDate.of(2000, 1, 1),
                120
        );
        when(filmService.getPopularFilms(5)).thenReturn(List.of(film));

        mockMvc.perform(get("/films/popular").param("count", "5"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(42))
                .andExpect(jsonPath("$[0].name").value("Test Film"))
                .andExpect(jsonPath("$[0].description").value("Description"))
                .andExpect(jsonPath("$[0].duration").value(120))
                .andExpect(jsonPath("$[0].releaseDate").value("2000-01-01"))
                .andExpect(jsonPath("$[0].mpa.id").value(1))
                .andExpect(jsonPath("$[0].mpa.name").value("R"))
                .andExpect(jsonPath("$[0].directors[0].id").value(1))
                .andExpect(jsonPath("$[0].directors[0].name").value("Danila Petrovich"))
                .andExpect(jsonPath("$[0].genres[0].id").value(1))
                .andExpect(jsonPath("$[0].genres[0].name").value("Комедия"));
    }

    @Test
    @DisplayName("GET /films/popular возвращает HTTP-ответ со статусом 400 и описанием ошибки 'count  не может быть отрицательным'")
    void getPopularFilms_ReturnsBadRequestWhenCountNegative() throws Exception {
        mockMvc.perform(get("/films/popular").param("count", "-1"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Ошибка валидации параметров"))
                .andExpect(jsonPath("$.errors.count").value("count  не может быть отрицательным"));

        verifyNoInteractions(filmService);
    }


    @Test
    @DisplayName("POST /films возвращает HTTP-ответ со статусом 201 и созданный film обратно в виде FilmResponseDto")
    void createFilm_ReturnsCreated() throws Exception {
        FilmRequestCreateDto requestCreateDto = new FilmRequestCreateDto(
                "Test Film",
                Set.of(new GenreRequestDto(1L)),
                new MpaRequestDto(1L),
                Set.of(new DirectorRequestDto(1L)),
                "Description",
                LocalDate.of(2000, 1, 1),
                120
        );

        FilmResponseDto film = new FilmResponseDto(
                42L,
                "Test Film",
                List.of(new GenreResponseDto(1, "Комедия")),
                new MpaResponseDto(1, "R"),
                List.of(new DirectorResponseDto(1, "Danila Petrovich")),
                "Description",
                LocalDate.of(2000, 1, 1),
                120
        );

        when(filmService.createFilm(any(FilmRequestCreateDto.class))).thenReturn(film);

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestCreateDto)))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(42))
                .andExpect(jsonPath("$.name").value("Test Film"))
                .andExpect(jsonPath("$.description").value("Description"))
                .andExpect(jsonPath("$.duration").value(120))
                .andExpect(jsonPath("$.releaseDate").value("2000-01-01"))
                .andExpect(jsonPath("$.mpa.id").value(1))
                .andExpect(jsonPath("$.mpa.name").value("R"))
                .andExpect(jsonPath("$.directors[0].id").value(1))
                .andExpect(jsonPath("$.directors[0].name").value("Danila Petrovich"))
                .andExpect(jsonPath("$.genres[0].id").value(1))
                .andExpect(jsonPath("$.genres[0].name").value("Комедия"));
    }


    @Test
    @DisplayName("POST /films возвращает HTTP-ответ со статусом 400 и сообщением 'releaseDate не может быть раньше чем 28.12.1895'")
    void createFilm_ReturnsBadRequestWhenReleaseDateTooEarly() throws Exception {

        FilmRequestCreateDto request = new FilmRequestCreateDto(
                "Test Film",
                Set.of(new GenreRequestDto(1L)),
                new MpaRequestDto(1L),
                Set.of(new DirectorRequestDto(1L)),
                "Description",
                LocalDate.of(1800, 1, 1),
                120
        );


        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Ошибка валидации входных данных"))
                .andExpect(jsonPath("$.errors.releaseDateValid").value("releaseDate не может быть раньше чем 28.12.1895"));

        verifyNoInteractions(filmService);
    }


    @Test
    @DisplayName("POST /films возвращает HTTP-ответ со статусом 400 и сообщением 'у description максимальная длина 200 символов'")
    void createFilm_ReturnsBadRequestWhenDescriptionTooLong() throws Exception {
        FilmRequestCreateDto request = new FilmRequestCreateDto(
                "Test Film",
                Set.of(new GenreRequestDto(1L)),
                new MpaRequestDto(1L),
                Set.of(new DirectorRequestDto(1L)),
                "a".repeat(201),
                LocalDate.of(2000, 1, 1),
                120
        );

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Ошибка валидации входных данных"))
                .andExpect(jsonPath("$.errors.description").value("у description максимальная длина 200 символов"));

        verifyNoInteractions(filmService);
    }


    @Test
    @DisplayName("POST /films возвращает HTTP-ответ со статусом 400 и сообщением 'name не должно состоять из пробелов'")
    void createFilm_ReturnsBadRequestWhenNameIsBlank() throws Exception {
        FilmRequestCreateDto request = new FilmRequestCreateDto(
                "",
                Set.of(new GenreRequestDto(1L)),
                new MpaRequestDto(1L),
                Set.of(new DirectorRequestDto(1L)),
                "Description",
                LocalDate.of(2000, 1, 1),
                120
        );

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Ошибка валидации входных данных"))
                .andExpect(jsonPath("$.errors.name").value("name не должно состоять из пробелов"));

        verifyNoInteractions(filmService);
    }


    @Test
    @DisplayName("POST /films возвращает HTTP-ответ со статусом 400 и сообщением 'duration не может быть пустым'")
    void createFilm_ReturnsBadRequestWhenDurationIsNull() throws Exception {
        FilmRequestCreateDto request = new FilmRequestCreateDto(
                "Test Film",
                Set.of(new GenreRequestDto(1L)),
                new MpaRequestDto(1L),
                Set.of(new DirectorRequestDto(1L)),
                "Description",
                LocalDate.of(2000, 1, 1),
                null
        );

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Ошибка валидации входных данных"))
                .andExpect(jsonPath("$.errors.duration").value("duration не может быть пустым"));

        verifyNoInteractions(filmService);
    }


    @Test
    @DisplayName("POST /films возвращает HTTP-ответ со статусом 400 и сообщением 'duration должна составлять не меньше 1 минуты'")
    void createFilm_ReturnsBadRequestWhenDurationIsNegative() throws Exception {
        FilmRequestCreateDto request = new FilmRequestCreateDto(
                "Test Film",
                Set.of(new GenreRequestDto(1L)),
                new MpaRequestDto(1L),
                Set.of(new DirectorRequestDto(1L)),
                "Description",
                LocalDate.of(2000, 1, 1),
                -1
        );

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Ошибка валидации входных данных"))
                .andExpect(jsonPath("$.errors.duration").value("duration должна составлять не меньше 1 минуты"));

        verifyNoInteractions(filmService);
    }


    @Test
    @DisplayName("PUT /film возвращает HTTP-ответ со статусом 200 и обновлённым FilmResponseDto")
    void updateFilm_ReturnsOk() throws Exception {
        FilmRequestUpdateDto request = new FilmRequestUpdateDto(
                42L,
                "Test Film",
                Set.of(new GenreRequestDto(1L)),
                new MpaRequestDto(1L),
                Set.of(new DirectorRequestDto(1L)),
                "Description",
                LocalDate.of(2000, 1, 1),
                120
        );

        FilmResponseDto film = new FilmResponseDto(
                42L,
                "Test Film",
                List.of(new GenreResponseDto(1, "Комедия")),
                new MpaResponseDto(1, "R"),
                List.of(new DirectorResponseDto(1L, "Danila Petrovich")),
                "Description",
                LocalDate.of(2000, 1, 1),
                120
        );

        when(filmService.updateFilm(any(FilmRequestUpdateDto.class))).thenReturn(film);

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

        verify(filmService).updateFilm(any(FilmRequestUpdateDto.class));
        verifyNoMoreInteractions(filmService);
    }

    @Test
    @DisplayName("GET /films возвращает HTTP-ответ со статусом 200 и коллекцией c FilmResponseDto")
    void getAllFilms_ReturnsOk() throws Exception {

        FilmResponseDto film = new FilmResponseDto(
                42L,
                "Test Film",
                List.of(new GenreResponseDto(1, "Комедия")),
                new MpaResponseDto(1, "R"),
                List.of(new DirectorResponseDto(1L, "Danila Petrovich")),
                "Description",
                LocalDate.of(2000, 1, 1),
                120
        );

        when(filmService.getAllFilms()).thenReturn(List.of(film));

        mockMvc.perform(get("/films"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(42))
                .andExpect(jsonPath("$[0].name").value("Test Film"))
                .andExpect(jsonPath("$[0].description").value("Description"))
                .andExpect(jsonPath("$[0].directors[0].id").value(1))
                .andExpect(jsonPath("$[0].directors[0].name").value("Danila Petrovich"))
                .andExpect(jsonPath("$[0].duration").value(120))
                .andExpect(jsonPath("$[0].releaseDate").value("2000-01-01"));

        verify(filmService).getAllFilms();
        verifyNoMoreInteractions(filmService);
    }

    @Test
    @DisplayName("GET /films/{id} возвращает HTTP-ответ со статусом 200 c FilmResponseDto")
    void getFilmById_ReturnsOk() throws Exception {
        FilmResponseDto film = new FilmResponseDto(
                42L,
                "Test Film",
                List.of(new GenreResponseDto(1, "Комедия")),
                new MpaResponseDto(1, "R"),
                List.of(new DirectorResponseDto(1L, "Danila Petrovich")),
                "Description",
                LocalDate.of(2000, 1, 1),
                120
        );

        when(filmService.getById(42L)).thenReturn(film);

        mockMvc.perform(get("/films/{id}", 42L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(42))
                .andExpect(jsonPath("$.name").value("Test Film"))
                .andExpect(jsonPath("$.description").value("Description"))
                .andExpect(jsonPath("$.directors[0].id").value(1))
                .andExpect(jsonPath("$.directors[0].name").value("Danila Petrovich"))
                .andExpect(jsonPath("$.duration").value(120))
                .andExpect(jsonPath("$.releaseDate").value("2000-01-01"));

        verify(filmService).getById(42L);
        verifyNoMoreInteractions(filmService);
    }


    @Test
    @DisplayName("GET /films/{id} возвращает HTTP-ответ со статусом 400 и сообщением 'id не может быть отрицательным'")
    void getFilmById_ReturnsBadRequestWhenIdIsNegative() throws Exception {
        mockMvc.perform(get("/films/{id}", -2L))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Ошибка валидации параметров"))
                .andExpect(jsonPath("$.errors.id").value("id не может быть отрицательным"));

        verifyNoInteractions(filmService);
    }
}