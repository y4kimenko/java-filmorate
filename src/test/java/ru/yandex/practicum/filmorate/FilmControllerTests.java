package ru.yandex.practicum.filmorate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.yandex.practicum.filmorate.controller.FilmController;

import java.util.Map;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class FilmControllerTests {

    private MockMvc mockMvc;
    private ObjectMapper mapper;

    @BeforeEach
    void setUp() {
        // Новый контроллер на каждый тест – независимое состояние
        FilmController controller = new FilmController();

        mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.disable(SerializationFeature.WRITE_DURATIONS_AS_TIMESTAMPS);

        MappingJackson2HttpMessageConverter conv =
                new MappingJackson2HttpMessageConverter(mapper);

        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setMessageConverters(conv)
                .build();
    }

//    @Test
//    void createFilm_ShouldReturn400_WhenIdIsNotBlank() throws Exception {
//        Map<String, Object> body = new HashMap<>();
//        body.put("name", " k ");
//        body.put("description", "Space epic");
//        body.put("releaseDate", "2014-11-07T00:00:00Z");
//        body.put("duration", "PT169M");
//
//        mockMvc.perform(post("/films")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(mapper.writeValueAsString(body)))
//                .andExpect(status().isBadRequest());
//    }

    @Test
    void updateFilm_WhenExists_ShouldApplyChanges() throws Exception {
        // Сначала создаём фильм id=1
        Map<String, Object> create = Map.of(
                "name", "Dune",
                "description", "Part One",
                "releaseDate", "2021-10-22T00:00:00Z",
                "duration", "PT155M"
        );
        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(create)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)));

        // Обновляем поля у id=1
        Map<String, Object> patchBody = Map.of(
                "id", 1,
                "name", "Dune – Updated",
                "description", "Part One – Extended",
                "releaseDate", "2021-10-23T00:00:00Z",
                "duration", "PT160M"
        );

        mockMvc.perform(patch("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(patchBody)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Dune – Updated")))
                .andExpect(jsonPath("$.description", is("Part One – Extended")))
                .andExpect(jsonPath("$.releaseDate", is("2021-10-23T00:00:00Z")))
                .andExpect(jsonPath("$.duration", is("PT2H40M")));
    }

    @Test
    void updateFilm_WhenNotExists_ShouldPropagateException() throws Exception {
        var patchBody = Map.of(
                "id", 999,
                "name", "Ghost",
                "description", "N/A",
                "releaseDate", "2000-01-01T00:00:00Z",
                "duration", "PT90M"
        );

        ServletException thrown = assertThrows(ServletException.class, () ->
                mockMvc.perform(patch("/films")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(patchBody)))
                        .andReturn() // важно вызвать, чтобы реально выполнить запрос
        );

        Throwable root = thrown.getCause();
        assertNotNull(root);

        assertTrue(root.getMessage().contains("Film not found"));
    }

    @Test
    void getAllFilms_ShouldReturnAllCreated() throws Exception {
        Map<String, Object> f1 = Map.of(
                "name", "Film A",
                "description", "A",
                "releaseDate", "2001-01-01T00:00:00Z",
                "duration", "PT100M"
        );
        Map<String, Object> f2 = Map.of(
                "name", "Film B",
                "description", "B",
                "releaseDate", "2002-02-02T00:00:00Z",
                "duration", "PT110M"
        );

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(f1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)));

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(f2)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(2)));

        mockMvc.perform(get("/films"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[1].id", is(2)));
    }

    // ───────── Дополнительные проверки валидации ─────────

    @Test
    void createFilm_ShouldFail_WhenNameBlank() throws Exception {
        Map<String, Object> body = Map.of(
                "name", "   ",
                "description", "desc",
                "releaseDate", "2014-11-07T00:00:00Z",
                "duration", "PT10M"
        );

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(body)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createFilm_ShouldFail_WhenDurationTooShort() throws Exception {
        Map<String, Object> body = Map.of(
                "name", "Short",
                "description", "desc",
                "releaseDate", "2020-01-01T00:00:00Z",
                "duration", "PT0S"
        );

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(body)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createFilm_ShouldFail_WhenReleaseDateTooEarly() throws Exception {
        // Требуется корректная реализация isReleaseDateValid()
        Map<String, Object> body = Map.of(
                "name", "Ancient",
                "description", "desc",
                "releaseDate", "1895-12-27T00:00:00Z",
                "duration", "PT60M"
        );

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(body)))
                .andExpect(status().isBadRequest());
    }
}
